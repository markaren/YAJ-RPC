package info.laht.yaj_rpc.ws

import info.laht.yaj_rpc.RpcResponse
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URI

class RpcWebSocketClient(
        private val host: String,
        private val port: Int
) {

    private val uri = URI("ws://$host:$port")

    private val ws = WebSocketClientImpl()

    fun send(methodName: String, params: List<*>?, callback: (RpcResponse) -> Unit) {

    }

    fun notify(methodName: String, params: List<*>? = null) {

    }

    inner class WebSocketClientImpl: WebSocketClient(uri) {

        override fun onOpen(handshakedata: ServerHandshake?) {

        }

        override fun onMessage(message: String?) {
            LOG.info(message)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {

        }

        override fun onError(ex: Exception) {
            LOG.error("WS error", ex)
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcWebSocketClient::class.java)
    }

}