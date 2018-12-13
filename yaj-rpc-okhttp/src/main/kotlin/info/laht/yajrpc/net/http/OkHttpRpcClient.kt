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

package info.laht.yajrpc.net.http

import info.laht.yajrpc.net.AbstractRpcClient
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

/**
 * @author ligi
 */
class OkHttpRpcClient @JvmOverloads constructor(
        host: String,
        port: Int,
        context: String = "jsonrpc",
        private val okHttpClient: OkHttpClient = OkHttpClient().newBuilder().build()
) : AbstractRpcClient() {

    private val url = "http://$host:$port/$context"

    private fun executeRequest(msg: String) = okHttpClient.newCall(Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("application/json-rpc"), msg))
            .build()).execute().body()?.string()
            ?: throw(IOException("Could not execute request to $url with $msg"))

    override fun internalWrite(msg: String) {
        messageReceived(executeRequest(msg))
    }

}
