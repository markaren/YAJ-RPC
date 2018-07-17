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

package info.laht.yajrpc.net.zmq

import info.laht.yajrpc.RpcHandler
import info.laht.yajrpc.net.RpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zeromq.ZContext
import org.zeromq.ZMQ

open class RpcZmqServer(
        private val handler: RpcHandler
) : RpcServer {

    override var port: Int? = null

    private var thread: Thread? = null
    private var ctx: ZContext? = null

    private var stop = false

    override fun start(port: Int) {

        if (thread == null) {

            this.port = port

            ctx = ZContext()
            val socket = ctx!!.createSocket(ZMQ.REP).apply {
                bind("tcp://*:$port")
            }

            thread = Thread {

                try {
                    while (!stop && !Thread.currentThread().isInterrupted) {

                        val recv = socket.recv(0)
                        recv ?: break

                        val data = String(recv, ZMQ.CHARSET)
                        LOG.trace(data)

                        handler.handle(data)?.also {
                            socket.send(it, 0)
                        } ?: socket.send("", 0)

                    }
                } catch (ex: Exception) {
                    LOG.trace("Caught exception", ex)
                } finally {
                    ctx?.destroy()
                    ctx = null
                }

            }.apply {
                start()
            }

            LOG.info("${javaClass.simpleName} listening for connections on port: $port")

        } else {
            LOG.warn("${javaClass.simpleName} is already running!")
        }

    }

    override fun stop() {

        if (thread != null) {
            LOG.debug("Stopping ${javaClass.simpleName} ...")
            stop = true
            ctx?.destroy()
            thread?.join(1000)
            thread = null
            LOG.info("${javaClass.simpleName} stopped!")
        }

    }

    private companion object {
        private val LOG: Logger = LoggerFactory.getLogger(RpcZmqServer::class.java)
    }

}