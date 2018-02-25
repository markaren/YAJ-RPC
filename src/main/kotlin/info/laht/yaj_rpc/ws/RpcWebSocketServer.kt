package info.laht.yaj_rpc.ws

import info.laht.yaj_rpc.RpcHandler
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.InetSocketAddress

class RpcWebSocketServer(
        val port: Int,
        val handler: RpcHandler
): AutoCloseable {

    private val ws = WebSocketServerImpl()

    fun start() = ws.start()
    fun stop() = ws.stop()

    override fun close() = stop()

    inner class WebSocketServerImpl: WebSocketServer(InetSocketAddress(port)) {

        override fun onStart() {

        }

        override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
            conn.send(handler.getOpenMessage())
        }

        override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
            LOG.info("Client disconnected")
        }

        override fun onMessage(conn: WebSocket, message: String) {
            handler.handle(message)?.also {response ->
                conn.send(response)
            }
        }



        override fun onError(conn: WebSocket?, ex: Exception) {
            LOG.error("WS error", ex)
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcWebSocketServer::class.java)
    }


}