package info.laht.yaj_rpc.net.http

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.net.RpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import java.nio.charset.Charset

class RpcHttpServer(
        val handler: RpcHandler
): SimpleHTTPServer(), RpcServer {

    override val context: String
        get() = "/jsonrpc"

    override val httpHandler: HttpHandler by lazy { MyHttpHandler() }

    inner class MyHttpHandler: HttpHandler {

        private fun noResponse(t: HttpExchange) {
            t.sendResponseHeaders(204, 0L).also {  t.responseBody.use {  }}
        }

        override fun handle(t: HttpExchange) {

            t.requestURI.query?.also { query ->

                val data = URLDecoder.decode(query, "UTF-8")
                handler.handle(data)?.also { response ->
                    val bytes = response.toByteArray(Charset.forName("UTF-8"))
                    t.sendResponseHeaders(200, bytes.size.toLong())
                    t.responseBody.use {
                        it.write(bytes)
                    }
                } ?: noResponse(t)



            }
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcHttpServer::class.java)
    }

}