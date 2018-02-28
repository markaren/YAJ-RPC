package info.laht.yaj_rpc.net.http

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress
import java.util.HashMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author laht
 */
abstract class SimpleHTTPServer {

    private var server: HttpServer? = null
    protected abstract val context: String
    protected abstract val httpHandler: HttpHandler

    @Throws(IOException::class)
    fun start(port: Int) {
        server = HttpServer.create(InetSocketAddress(port), 0).apply {
            createContext(context, httpHandler)
            executor = null
            start()
        }

        LOG.info("Serving http on port: $port")
    }

    fun stop() {
        server?.apply {
            stop(0)
            LOG.info("Http server stopped!")
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