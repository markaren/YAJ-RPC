package info.laht.yaj_rpc.ws

import com.google.gson.annotations.SerializedName
import info.laht.yaj_rpc.*
import info.laht.yaj_rpc.parser.JsonParser
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URI
import java.util.*
import java.util.concurrent.CountDownLatch

typealias RpcCallback = (RpcResponse) -> Unit

class RpcWebSocketClient(
        private val host: String,
        private val port: Int
): AutoCloseable {

    private val uri = URI("ws://$host:$port")
    private val callbacks = mutableMapOf<String, RpcCallback>()

    private var connectionInitiated = false
    private val ws = WebSocketClientImpl()

    fun connectAsync() = ws.connect().also { connectionInitiated = true }
    fun connect() = ws.connectBlocking().also { connectionInitiated = true }

    override fun close() = ws.closeBlocking()
    fun closeAsync() = ws.close()


    @JvmOverloads
    fun notify(methodName: String, params: RpcParams = RpcNoParams) {
        if (!connectionInitiated) throw IllegalStateException("Forgot to call connect?")

        ws.send(RpcRequest(methodName, params).let { it.toJson() })
    }

    @JvmOverloads
    fun writeAsync(methodName: String, params: RpcParams = RpcNoParams, callback: RpcCallback) {
        if (!connectionInitiated) throw IllegalStateException("Forgot to call connect?")

        val request = RpcRequest(methodName, params).apply {
            id = UUID.randomUUID().toString()
            callbacks[id.toString()] = callback
        }.let { it.toJson() }
        ws.send(request)
    }

    @JvmOverloads
    fun write(methodName: String, params: RpcParams = RpcNoParams): RpcResponse {
        if (!connectionInitiated) throw IllegalStateException("Forgot to call connect?")

        var response: RpcResponse? = null
        val latch = CountDownLatch(1)
        val request = RpcRequest(methodName, params).apply {
            id = UUID.randomUUID().toString()
            callbacks[id.toString()] = {
                response = it
                latch.countDown()
            }
        }.let { it.toJson() }
        ws.send(request)
        latch.await()
        return response!!
    }

    inner class WebSocketClientImpl: WebSocketClient(uri) {

        override fun onOpen(handshakedata: ServerHandshake?) {
            LOG.info("WS client connected")
        }

        override fun onMessage(message: String) {
            val response = RpcResponse.fromJson(message)
            if (response.error != null) {
                LOG.warn("RPC invocation returned error: ${response.error}")
            } else {
                val id = response.id.toString()
                callbacks[id]?.also { callback ->
                    callback.invoke(response)
                }
                callbacks.remove(id)
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            LOG.info("WS client closed connection..")
        }

        override fun onError(ex: Exception) {
            LOG.error("WS error", ex)
        }
    }

    inner class RpcRequest(
            @SerializedName("method")
            val methodName: String,
            val params: RpcParams = RpcNoParams
    ) {

        var id = NO_ID
        @SerializedName("jsonrpc")
        var version = JSON_RPC_VERSION

        fun toJson(): String {
            return JsonParser.gson.toJson(this)
        }

    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcWebSocketClient::class.java)
    }

}