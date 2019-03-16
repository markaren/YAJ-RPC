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

package info.laht.yajrpc.net.ws

import info.laht.yajrpc.RpcHandler
import info.laht.yajrpc.net.RpcServer
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.InetSocketAddress
import java.util.*

/**
 * @author Lars Ivar Hatledal
 */
open class RpcWebSocketServer @JvmOverloads constructor(
        private val handler: RpcHandler,
        private val reuseAddress: Boolean = true
) : RpcServer {

    override var port: Int? = null

    private var ws: WebSocketServerImpl? = null
    private val clients = Collections.synchronizedSet(mutableSetOf<WebSocket>())

    override fun start(port: Int) {
        if (ws == null) {
            this.port = port
            ws = WebSocketServerImpl(port).also {
                it.isReuseAddr = reuseAddress
                it.start()
                LOG.info("${javaClass.simpleName} listening for connections on port: $port")
            }
        } else {
            LOG.warn("${javaClass.simpleName} is already running!")
        }
    }

    override fun stop() {
        ws?.also { server ->
            synchronized(clients) {
                clients.forEach { client ->
                    client.close()
                }
            }
            server.stop()
            ws = null
            LOG.info("${javaClass.simpleName} stopped!")
        }
    }

    inner class WebSocketServerImpl(
            port: Int
    ) : WebSocketServer(InetSocketAddress(port)) {

        override fun onStart() {
            LOG.debug("onStart")
        }

        override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
            LOG.info("Client with address ${conn.remoteSocketAddress} connected")
            conn.send(handler.getOpenMessage())
            synchronized(clients) {
                clients.add(conn)
            }
        }

        override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
            LOG.info("Client with address ${conn.remoteSocketAddress} disconnected")
            synchronized(clients) {
                clients.remove(conn)
            }
        }

        override fun onMessage(conn: WebSocket, message: String) {
            LOG.trace("Received: $message")
            handler.handle(message)?.also { response ->
                conn.send(response)
            }
        }

        override fun onError(conn: WebSocket?, ex: Exception) {
            LOG.error("onError, conn=$conn", ex)
        }
    }

    private companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcWebSocketServer::class.java)
    }

}