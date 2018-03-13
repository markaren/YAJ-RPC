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

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.net.RpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.util.HashMap


abstract class SimpleHTTPServer {

    var port: Int? = null

    private var server: HttpServer? = null
    protected abstract val context: String
    protected abstract val httpHandler: HttpHandler

    @Throws(IOException::class)
    fun start(port: Int) {

        if (server == null) {
            this.port = port
            server = HttpServer.create(InetSocketAddress(port), 0).apply {
                createContext(context, httpHandler)
                executor = null
                start()
            }
            LOG.info("${javaClass.simpleName} for connections on port: $port")
        } else {
            LOG.warn("${javaClass.simpleName} is already running!")
        }

    }

    fun stop() {
        server?.apply {
            stop(0)
            LOG.info("${javaClass.simpleName} stopped!")
            server = null
        }
    }

    protected fun queryToMap(query: String): Map<String, String> {

        val result = HashMap<String, String>()
        for (param in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val pair = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (pair.size > 1) {
                result[pair[0]] = pair[1]
            } else {
                result[pair[0]] = ""
            }
        }
        return result
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(SimpleHTTPServer::class.java)
    }


}

open class RpcHttpServer(
        val handler: RpcHandler
): SimpleHTTPServer(), RpcServer {

    val legalContentTypes =
        arrayOf("application/json-rpc",
                "application/json",
                "application/jsonrequest")


    override val context: String
        get() = "/jsonrpc"

    override val httpHandler: HttpHandler by lazy { MyHttpHandler() }

    private inner class MyHttpHandler: HttpHandler {

        private fun noResponse(t: HttpExchange) {
            t.sendResponseHeaders(204, 0L).also {  t.responseBody.use {  }}
        }

        override fun handle(t: HttpExchange) {

            val contentType: String = t.requestHeaders.getFirst("Content-Type")

            if (contentType in legalContentTypes) {
                val data = t.requestBody.use {
                    val out = ByteArrayOutputStream()
                    val buf = ByteArray(4096)
                    var n = it.read(buf)
                    while (n > 0) {
                        out.write(buf, 0, n)
                        n = it.read(buf)
                    }
                    String(out.toByteArray(), Charset.forName("UTF-8"))
                }

                LOG.debug("Received: $data")
                handler.handle(data)?.also { response ->
                    val bytes = response.toByteArray(Charset.forName("UTF-8"))
                    t.sendResponseHeaders(200, bytes.size.toLong())
                    t.responseBody.use {
                        it.write(bytes)
                    }
                } ?: noResponse(t)
            } else {
                LOG.warn("Unsupported content type: '$contentType'")
                noResponse(t)
            }

        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcHttpServer::class.java)
    }

}