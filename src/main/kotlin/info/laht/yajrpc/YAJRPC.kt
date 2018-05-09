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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Type

const val JSON_RPC_VERSION = "2.0"
const val JSON_RPC_IDENTIFIER = "jsonrpc"
const val METHOD_KEY = "method"
const val PARAMS_KEY = "params"
const val ID_KEY = "id"

const val DATA_KEY = "data"
const val MESSAGE_KEY = "message"
const val METHOD_NOT_FOUND_KEY = "method not found"
const val PARSE_ERROR_KEY = "parse error"
const val INVALID_REQUEST_KEY = "invalid request"
const val CODE_KEY = "code"
const val ERROR_KEY = "error"
const val RESULT_KEY = "result"

val NO_ID = Unit

const val METHOD_NOT_FOUND_CODE = -32601
const val PARSE_ERROR_CODE = -32700
const val INVALID_REQUEST_CODE = -32600
const val INVALID_PARAMS_CODE = -32602
const val INTERNAL_ERROR_CODE = -32603

/**
 *
 * @author Lars Ivar Hatledal
 */
object YAJRPC {

    private val LOG: Logger = LoggerFactory.getLogger(YAJRPC::class.java)

    private val builder = GsonBuilder()
            .registerTypeAdapter(RpcParams::class.java, RpcParamsTypeAdapter())
            .serializeNulls()

    val jsonParser: Gson by lazy {
        builder.create()
    }

    fun toJson(`object`: Any?): String = jsonParser.toJson(`object`)

    internal fun toJsonTree(any: Any): JsonElement = jsonParser.toJsonTree(any)

    internal fun <T> fromJson(json: JsonElement, type: Class<T>) = jsonParser.fromJson(json, type)

    fun <T> fromJson(json: String, type: Class<T>): T = jsonParser.fromJson(json, type)

    inline fun <reified T> fromJson(json: String): T = jsonParser.fromJson(json, T::class.java)

    @JvmStatic
    fun registerTypeAdapter(type: Type, typeAdapter: Any) {
        builder.registerTypeAdapter(type, typeAdapter)
        LOG.debug("Registered jsonParser type adapter for type '$type'")
    }

}


