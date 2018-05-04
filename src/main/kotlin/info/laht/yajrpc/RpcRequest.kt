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

import com.google.gson.annotations.SerializedName

/**
 * @author Lars Ivar Hatledal
 */
interface RpcRequest {

    val id: Any
    val version: String?
    val methodName: String?
    val params: RpcParams

    val isNotification
        get() = id == NO_ID


    companion object {

        fun fromJson(json: String): RpcRequest {
            return YAJ_RPC.fromJson(json, RpcRequestIn::class.java)
        }

    }

}

class RpcRequestOut(
        @SerializedName(METHOD_KEY)
        val methodName: String,
        val params: RpcParams = RpcNoParams
) {

    var id: Any = NO_ID

    @SerializedName(JSON_RPC_IDENTIFIER)
    var version = JSON_RPC_VERSION

    fun toJson(): String {
        return YAJ_RPC.toJson(this)
    }

}

/**
 * @author Lars Ivar Hatledal
 */
class RpcRequestIn internal constructor() : RpcRequest {

    override val id: Any = NO_ID

    @SerializedName(value = METHOD_KEY)
    override val methodName: String? = null

    @SerializedName(JSON_RPC_IDENTIFIER)
    override val version: String? = null

    @SerializedName(PARAMS_KEY)
    private val _params: RpcParams? = null

    override val params: RpcParams
        get() = _params ?: RpcNoParams

    override fun toString(): String {
        return mutableListOf<String>().apply {

            version?.also { add("$JSON_RPC_IDENTIFIER=$it") }
            methodName?.also { add("$METHOD_KEY=$it") }
            add("$PARAMS_KEY=$params")
            if (id !== NO_ID) {
                add("$ID_KEY=$id")
            }
            add("isNotification=$isNotification")

        }.joinToString(", ").let { "RpcRequestIn($it)" }

    }

}
