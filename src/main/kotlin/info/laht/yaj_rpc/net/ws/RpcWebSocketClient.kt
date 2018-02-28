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

package info.laht.yaj_rpc.net.ws

import info.laht.yaj_rpc.net.AbstractRpcClient
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URI


open class RpcWebSocketClient(
        host: String,
        port: Int
): AbstractRpcClient() {

    private val uri = URI("ws://$host:$port")
    private val ws = WebSocketClientImpl()

    init {
        ws.connectBlocking()
    }

    override fun close() = ws.closeBlocking()

    override fun write(msg: String, isNotification: Boolean) = ws.send(msg)

    inner class WebSocketClientImpl: WebSocketClient(uri) {

        override fun onOpen(handshake: ServerHandshake?) {
            LOG.info("WS client connected")
        }

        override fun onMessage(message: String)
                = messageReceived(message)

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            LOG.info("WS client closed connection..")
        }

        override fun onError(ex: Exception) {
            LOG.error("WS error", ex)
        }

    }

    private companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcWebSocketClient::class.java)
    }

}