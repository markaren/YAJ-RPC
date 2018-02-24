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

val JSON_RPC_VERSION = "2.0"
val JSON_RPC_IDENTIFIER = "jsonrpc"
val METHOD_KEY = "method"
val PARAMS_KEY = "params"
val ID_KEY = "id"

val DATA_KEY = "data"
val MESSAGE_KEY = "message"
val METHOD_NOT_FOUND_KEY = "method not found"
val PARSE_ERROR_KEY = "parse error"
val INVALID_REQUEST_KEY = "invalid request"
val CODE_KEY = "code"
val ERROR_KEY = "error"
val RESULT_KEY = "result"

val NO_ID = Any()

val METHOD_NOT_FOUND_CODE = -32601
val PARSE_ERROR_CODE = -32700
val INVALID_REQUEST_CODE = -32600