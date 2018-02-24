/*
 * The MIT License
 *
 * Copyright 2018 Lars Ivar Hatledal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package info.laht.yaj_rpc

import com.google.gson.Gson
import com.google.gson.JsonElement
import info.laht.yaj_rpc.parser.JsonParser
import java.lang.reflect.Method
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias Converter<E> = (JsonElement) -> E

class RpcHandler {

    private val services = mutableMapOf<String, RpcService>()
    private val converters = mutableMapOf<Class<*>, Converter<*>>()

    fun addService(service: RpcService) {
        services[service.name] = service
    }

    fun removeService(service: RpcService) {
        services.remove(service.name)
    }

    fun addConverter(t: Class<*>, converter: Converter<*>) {
        converters[t] = converter
    }

    fun getOpenMessage(): String {
        return services.entries.associate {
            it.key to it.value.getCallDescription()
        }.let { JsonParser.toJson(it) }
    }

    fun handle(json: String): String? {
        val req: RpcRequestImpl
        try {
            req = JsonParser.parseRequest(json)
        } catch (ex: Exception) {
            val msg = "Exception encountered while handling json string: $json"
            LOG.error(msg, ex)
            return createErrorResponse(null, RpcError.ErrorType.PARSE_ERROR, msg)
        }

        val id = req.id
        if (req.version != JSON_RPC_VERSION) {
            val msg = "Wrong or invalid jsonrpc version: ${req.version}"
            LOG.warn(msg)
            return createErrorResponse(id, RpcError.ErrorType.INVALID_REQUEST, msg)
        }
        if (req.methodName == null) {
            val msg = "No method specified!"
            LOG.warn(msg)
            return createErrorResponse(id, RpcError.ErrorType.INVALID_REQUEST, msg)
        }
        return handle(req)
    }

    private fun handle(req: RpcRequestImpl): String? {
        val id = req.id
        val split = req.methodName!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (split.size != 2) {
            val msg = "method does not use '.' to separate service and method"
            LOG.warn(msg)
            return createErrorResponse(id, RpcError.ErrorType.INVALID_REQUEST, msg)
        }
        val serviceName = split[0]
        if (!services.containsKey(serviceName)) {
            val msg = "no such registered service '$serviceName'"
            LOG.warn(msg)
            return createErrorResponse(id, RpcError.ErrorType.METHOD_NOT_FOUND, msg)
        }
        val service = services[serviceName]!!
        val methodName = split[1]
        val paramCount = req.params.paramCount
        val method = service.getExposedMethod(methodName, paramCount)
        if (method == null) {
            val msg = "no such method '$methodName' in service '$serviceName' that takes $paramCount params"
            LOG.warn(msg)
            return createErrorResponse(id, RpcError.ErrorType.METHOD_NOT_FOUND, msg)
        }
        val params = req.params
        return when(params) {
            RpcNoParams -> handleNoParams(service, method, id, req.isNotification)
            is RpcListParams -> handleListParams(service, method, params.value, id, req.isNotification)
            is RpcMapParams -> handleMapParams(service, method, params.value, id, req.isNotification)
        }


    }

    private fun handleNoParams(service: RpcService, method: Method, id: Any, isNotification: Boolean): String? {
        if (method.parameterCount > 0) {
            return createErrorResponse(id, RpcError.ErrorType.METHOD_NOT_FOUND)
        }
        return try {
            if (isNotification) null else createResponse(method.invoke(service), id)
        } catch (ex: Exception) {
            val msg = "Exception encountered while invoking method ${method.name}"
            LOG.error(msg, ex)
            createErrorResponse(null, RpcError.ErrorType.INTERNAL_ERROR, msg)
        }

    }

    private fun handleListParams(service: RpcService, method: Method, params: MutableList<JsonElement>, id: Any, isNotification: Boolean): String? {

        if (params.size != method.parameterCount) {
            return createErrorResponse(id, RpcError.ErrorType.INVALID_PARAMS, "params.length != method.getParameterCount()")
        }

        try {

            val typedParams = toTypedParams(params, method.parameterTypes)
            return try {
                if (isNotification) {
                    null
                } else {
                    val invoke = method.invoke(service, *typedParams.toTypedArray()) ?: "void"
                    createResponse (invoke, id)
                }
            } catch (ex: Exception) {
                val msg = "Exception encountered while invoking method ${method.name}"
                LOG.error(msg, ex)
                createErrorResponse(id, RpcError.ErrorType.INTERNAL_ERROR, msg)
            }

        } catch (ex: AssertionError) {
            val msg = "Failed to match json types to method parameters"
            LOG.warn(msg, ex)
            return createErrorResponse(id, RpcError.ErrorType.INTERNAL_ERROR, msg)
        }

    }

    private fun handleMapParams(service: RpcService, method: Method, params: Map<String, JsonElement>, id: Any, isNotification: Boolean): String? {

        if (method.parameterCount != params.size) {
            val msg = "Number of method parameters and params does not match '$method'"
            LOG.error(msg)
            return createErrorResponse(id, RpcError.ErrorType.INVALID_PARAMS, msg)
        }
        val collect = params.keys.map({ key -> indexOf(key, method) })
        if (collect.contains(-1)) {
            val msg = "mismatch between one or more parameter names and params keys, params: $params, parameterNames: ${getParameterNames(method)}"
            LOG.error(msg)
            return createErrorResponse(id, RpcError.ErrorType.INVALID_PARAMS, msg)
        }
        val args = MutableList<JsonElement>(params.size, {i -> getValueByIndex(collect[i], params)})
        return handleListParams(service, method, args, id, isNotification)
    }

    private fun toTypedParams(params: List<JsonElement>, types: Array<Class<*>>): List<Any> {

        if (params.size != types.size) {
            throw AssertionError("params.length != types.length")
        }

        return List<Any>(types.size, {i->
            val arg = types[i]
            when {
                converters.containsKey(arg) -> converters[arg]!!.invoke(params[i]!!)!!
                arg == Boolean::class.java || arg == Boolean::class.javaPrimitiveType -> params[i].asBoolean
                arg == Long::class.java || arg == Long::class.javaPrimitiveType -> params[i].asLong
                arg == Int::class.java || arg == Int::class.javaPrimitiveType -> params[i].asInt
                arg == Float::class.java || arg == Float::class.javaPrimitiveType -> params[i].asFloat
                arg == Double::class.java || arg == Double::class.javaPrimitiveType -> params[i].asDouble
                params[i].isJsonObject -> Gson().fromJson(params[i], arg)
                else -> throw AssertionError()
            }
        })

    }


    private companion object {

        val LOG: Logger = LoggerFactory.getLogger(RpcHandler::class.java)

        fun createErrorResponse(id: Any, errorType: RpcError.ErrorType): String {
            return createErrorResponse(id, errorType, null)
        }

        fun createErrorResponse(id: Any?, errorType: RpcError.ErrorType, data: Any?): String {
            return mutableMapOf<String, Any?>().also {
                it[JSON_RPC_IDENTIFIER] = JSON_RPC_VERSION
                it[ERROR_KEY] = errorType.toMap(data)
                it[ID_KEY] = id
            }.let { JsonParser.toJson(it) }
        }

        fun createResponse(result: Any, id: Any): String {
            return mutableMapOf<String, Any>().also {
                it[JSON_RPC_IDENTIFIER] = JSON_RPC_VERSION
                it[RESULT_KEY] = JsonParser.toJson(result)
                it[ID_KEY] = id
            }.let { JsonParser.toJson(it) }
        }

    }

}