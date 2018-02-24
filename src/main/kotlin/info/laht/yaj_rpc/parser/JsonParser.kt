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

package info.laht.yaj_rpc.parser

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import info.laht.yaj_rpc.RpcParams
import info.laht.yaj_rpc.RpcParamsDeserializer
import info.laht.yaj_rpc.RpcRequestImpl
import info.laht.yaj_rpc.RpcResponse
import java.lang.reflect.Type
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Lars Ivar Hatledal
 */
object JsonParser {

    private val LOG: Logger = LoggerFactory.getLogger(JsonParser::class.java)

    private var gson: Gson? = null
    private val builder = GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(RpcParams::class.java, RpcParamsDeserializer())
            .setPrettyPrinting()

    private var changed = true

    private fun getGson(): Gson {
        if (changed) {
            gson = builder.create()
            changed = false
        }
        return gson!!
    }

    fun registerTypeAdapter(type: Type, typeAdapter: Any) {
        builder.registerTypeAdapter(type, typeAdapter)
        changed = true
    }

    fun toJson(`object`: Any): String {
        return getGson().toJson(`object`)
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T {
        return getGson().fromJson(json, clazz)
    }

    fun parseRequest(json: String): RpcRequestImpl {
        return getGson().fromJson(json, RpcRequestImpl::class.java)
    }

    fun parseResponse(json: String): RpcResponse? {
        try {
            return getGson().fromJson(json, RpcResponse::class.java)
        } catch (ex: IllegalStateException) {
            LOG.error("Failed to parse response from: {}", json, ex)
        }

        return null
    }

}
