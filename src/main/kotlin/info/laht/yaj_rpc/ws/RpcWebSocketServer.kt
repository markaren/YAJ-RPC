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
            LOG.debug("onStart")
        }

        override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
            LOG.info("Connection to server established")
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