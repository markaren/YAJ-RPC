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

import com.google.gson.annotations.SerializedName
import info.laht.yaj_rpc.parser.JsonParser

interface RpcRequest {
    val id: Any
    val version: String?
    val methodName: String?
    val params: RpcParams

    val isNotification
        get() = id == NO_ID


    companion object {

        fun fromJson(json: String): RpcRequest {
            return JsonParser.gson.fromJson(json, RpcRequestImpl::class.java)
        }

        fun toJson(request: RpcRequest): String {
            return JsonParser.gson.toJson(request)
        }

    }

}

/**
 * @author Lars Ivar Hatledal laht@ntnu.no.
 */
class RpcRequestImpl internal constructor(): RpcRequest {

    override val id = NO_ID

    @SerializedName(value = "method")
    override val methodName: String? = null

    @SerializedName("jsonrpc")
    override val version: String? = null

    @SerializedName("params")
    private val _params: RpcParams? = null

    override val params: RpcParams
        get() = _params ?: RpcNoParams

    override fun toString(): String {
        return mutableListOf<String>().apply {

            version?.also { add("jsonrpc=$it") }
            methodName?.also { add("method=$it") }
            add("params=$params")
            if (id !== NO_ID) {
                add("id=$id")
            }
            add("isNotification=$isNotification")

        }.joinToString(", ").let { "JsonRPCRequestImpl($it)" }

    }

}
