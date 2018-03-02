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
import com.google.gson.annotations.SerializedName

interface RpcResponse {

    val id: Any
    val version: String?
    val error: RpcError?

    fun <T> getResult(clazz: Class<T>): T?

    companion object {

        fun fromJson(json: String): RpcResponse {
            return YAJ_RPC.fromJson(json, RpcResponseImpl::class.java)
        }

    }

}

/**
 *
 * @author Lars Ivar Hatledal
 */
class RpcResponseImpl internal constructor(): RpcResponse {

    @SerializedName("jsonrpc")
    override val version: String? = null

    override val id: Any = NO_ID
    override val error: RpcError? = null
    private val result: String? = null

    val isVoid: Boolean
        get() = hasResult && result == null

    val hasError: Boolean
        get() = error != null


    val hasResult: Boolean
        get() = !hasError

    override fun <T> getResult(clazz: Class<T>): T? {
        return if (hasResult) {
            Gson().fromJson(result, clazz)
        } else null
    }

    override fun toString(): String {
        return "JsonRPCResponse{" + "jsonrpc=" + version + ", id=" + id + ", response=" + (if (hasError) error else result) + '}'.toString()
    }
}