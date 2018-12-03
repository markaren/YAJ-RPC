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

package info.laht.yajrpc

import com.google.gson.JsonElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

/**
 * @author Lars Ivar Hatledal
 */
class RpcHandler private constructor(
        private val services: Map<String, RpcService>
) {

    constructor(vararg services: RpcService) : this(services.associateBy { it.serviceName })
    constructor(services: List<RpcService>) : this(services.associateBy { it.serviceName })

    init {
        if (services.isEmpty()) {
            throw IllegalArgumentException("No services provided!")
        }
    }

    fun getOpenMessage(): String {
        return services.entries.associate {
            it.key to RpcService.getCallDescription(it.value)
        }.let { YAJRPC.toJson(it) }
    }

    fun handle(json: String): String? {
        val req: RpcRequest
        try {
            req = RpcRequest.fromJson(json)
        } catch (ex: Exception) {
            val msg = "Exception encountered while parsing json string: $json"
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

    @Suppress("UNCHECKED_CAST")
    private fun handle(req: RpcRequest): String? {
        val id = req.id
        val split = req.methodName!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (split.size != 2) {
            val msg = "Method does not use '.' to separate service and method"
            LOG.warn(msg)
            return createErrorResponse(id, RpcError.ErrorType.INVALID_REQUEST, msg)
        }
        val serviceName = split[0]
        if (!services.containsKey(serviceName)) {
            val msg = "No such registered service '$serviceName'"
            LOG.warn(msg)
            return createErrorResponse(id, RpcError.ErrorType.METHOD_NOT_FOUND, msg)
        }
        val service = services[serviceName]!!
        val methodName = split[1]
        val paramCount = req.params.paramCount
        val method = RpcService.getExposedMethod(service, methodName, paramCount)
        if (method == null) {
            val msg = "No such method '$methodName' in service '$serviceName' that takes $paramCount params"
            LOG.warn(msg)
            return createErrorResponse(id, RpcError.ErrorType.METHOD_NOT_FOUND, msg)
        }
        return when (val params = req.params) {
            RpcNoParams -> handleNoParams(service, method, id, req.isNotification)
            is RpcListParams<*> -> handleListParams(service, method, params.value as List<JsonElement>, id, req.isNotification)
            is RpcMapParams<*> -> handleMapParams(service, method, params.value as Map<String, JsonElement>, id, req.isNotification)
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

    private fun handleListParams(service: RpcService, method: Method, params: List<JsonElement>, id: Any, isNotification: Boolean): String? {

        if (params.size != method.parameterCount) {
            return createErrorResponse(id, RpcError.ErrorType.INVALID_PARAMS, "params.length != method.getParameterCount()")
        }

        try {

            val typedParams = toTypedParams(params, method.parameterTypes)
            return try {
                if (isNotification) {
                    null
                } else {
                    val invoke = method.invoke(service, *typedParams.toTypedArray())
                    createResponse(invoke, id)
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
        val collect = params.keys.map({ key -> method.indexOf(key) })
        if (collect.contains(-1)) {
            val parameterNames = method.parameters.map { it.name }
            val msg = "Mismatch between one or more parameter names and params keys, params: $params, parameterNames: $parameterNames"
            LOG.error(msg)
            return createErrorResponse(id, RpcError.ErrorType.INVALID_PARAMS, msg)
        }
        return MutableList(params.size) { i -> params.getValueByIndex(collect[i]) }.let {
            handleListParams(service, method, it, id, isNotification)
        }
    }

    private fun toTypedParams(params: List<JsonElement>, types: Array<Class<*>>): List<Any> {

        if (params.size != types.size) {
            throw AssertionError("params.length != types.length")
        }

        return List(types.size) { i ->
            val arg = types[i]
            val param = params[i]
            when (arg) {
                Boolean::class.java, Boolean::class.javaPrimitiveType -> param.asBoolean
                Long::class.java, Long::class.javaPrimitiveType -> param.asLong
                Int::class.java, Int::class.javaPrimitiveType -> param.asInt
                Float::class.java, Float::class.javaPrimitiveType -> param.asFloat
                Double::class.java, Double::class.javaPrimitiveType -> param.asDouble
                else -> YAJRPC.fromJson(param, arg)
            }
        }

    }


    private companion object {

        private val LOG: Logger = LoggerFactory.getLogger(RpcHandler::class.java)

        fun createErrorResponse(id: Any, errorType: RpcError.ErrorType): String {
            return createErrorResponse(id, errorType, null)
        }

        fun createErrorResponse(id: Any?, errorType: RpcError.ErrorType, data: Any?): String {
            return mutableMapOf<String, Any?>().also {
                it[JSON_RPC_IDENTIFIER] = JSON_RPC_VERSION
                it[ERROR_KEY] = errorType.toMap(data)
                it[ID_KEY] = id
            }.let { YAJRPC.toJson(it) }
        }

        fun createResponse(result: Any?, id: Any): String {
            return mutableMapOf<String, Any>().also {
                it[JSON_RPC_IDENTIFIER] = JSON_RPC_VERSION
                it[RESULT_KEY] = YAJRPC.toJson(result)
                it[ID_KEY] = id
            }.let { YAJRPC.toJson(it) }
        }

    }

}