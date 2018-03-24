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

package info.laht.yaj_rpc.net.http

import info.laht.yaj_rpc.net.AbstractRpcClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset


class RpcHttpClient(
         host: String,
         port: Int
): AbstractRpcClient() {

    private val url = "http://$host:$port/jsonrpc"

    private fun connect(msg: String): String {
        val con = (URL(url).openConnection() as HttpURLConnection).apply {
            setRequestProperty("Content-Type", "application/json-rpc")
            requestMethod = "POST"
            doOutput = true
            outputStream.use {
                it.write(msg.toByteArray(Charset.forName("UTF-8")))
                it.flush()
            }
            connect()
        }
        return StringBuilder().apply {
            BufferedReader(InputStreamReader(con.inputStream)).use {
                while (true) {
                    it.readLine()?.also { append(it) } ?: break
                }
            }
        }.toString()
    }

    override fun internalWrite(msg: String) {
       messageReceived( connect(msg))
    }

    /**
     * This method has no effect for
     * instances of RpcHttpClient
     */
    override fun close() {
        //nothing to do
    }
}

