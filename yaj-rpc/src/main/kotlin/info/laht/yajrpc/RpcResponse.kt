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
import com.google.gson.annotations.SerializedName

/**
 *
 * @author Lars Ivar Hatledal
 */
class RpcResponse internal constructor() {

    @SerializedName(JSON_RPC_IDENTIFIER)
    val version: String? = null

    val id: Any = NO_ID
    val error: RpcError? = null
    private val result: Any? = null

    val isVoid: Boolean
        get() = hasResult && result == null

    val hasError: Boolean
        get() = error != null

    val hasResult: Boolean
        get() = !hasError

    inline fun <reified T> getResult(): T? {
        return getResult(T::class.java)
    }

    fun <T> getResult(clazz: Class<T>): T? {
        return if (hasResult && !isVoid) {
           YAJRPC.fromJson(YAJRPC.toJson(result!!), clazz)
        } else null
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun toString(): String {
        return "JsonRPCResponse{" + "$JSON_RPC_IDENTIFIER=$version, $ID_KEY=$id, response=${(if (hasError) error else result)}}"
    }
}