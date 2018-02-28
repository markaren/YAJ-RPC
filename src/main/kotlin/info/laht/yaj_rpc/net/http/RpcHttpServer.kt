package info.laht.yaj_rpc.net.http

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.net.RpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset


class RpcHttpServer(
        val handler: RpcHandler
): SimpleHTTPServer(), RpcServer {

    val legalContentTypes by lazy {
        listOf("application/json-rpc",
                "application/json")
    }

    override val context: String
        get() = "/jsonrpc"

    override val httpHandler: HttpHandler by lazy { MyHttpHandler() }

    inner class MyHttpHandler: HttpHandler {

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
                noResponse(t)
            }

        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcHttpServer::class.java)
    }

}