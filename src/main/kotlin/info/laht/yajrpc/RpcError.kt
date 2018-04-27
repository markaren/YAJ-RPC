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


import java.util.*

/**
 * @author Lars Ivar Hatledal
 */
class RpcError @JvmOverloads internal constructor(
        private val code: Int,
        private val message: String,
        private val data: Any? = null
) {

    enum class ErrorType constructor(
            private val code: Int
    ) {

        METHOD_NOT_FOUND(METHOD_NOT_FOUND_CODE),
        PARSE_ERROR(PARSE_ERROR_CODE),
        INVALID_REQUEST(INVALID_REQUEST_CODE),
        INVALID_PARAMS(INVALID_PARAMS_CODE),
        INTERNAL_ERROR(INTERNAL_ERROR_CODE);

        fun toMap(data: Any?): MutableMap<String, Any> {
            return HashMap<String, Any>().also {
                it[CODE_KEY] = code
                it[MESSAGE_KEY] = toString().toLowerCase().replace("_".toRegex(), " ")
                if (data != null) {
                    it[DATA_KEY] = data
                }
            }
        }
    }

    override fun toString(): String {
        return "RpcError(code=$code, message='$message', data=$data)"
    }


}
