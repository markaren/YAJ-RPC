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


import info.laht.yajrpc.net.AbstractRpcClient
import org.zeromq.ZContext
import org.zeromq.ZMQ

class RpcZmqClient(
        private val host: String,
        private val port: Int
) : AbstractRpcClient() {

    private val ctx = ZContext(1)
    private val socket = ctx.createSocket(ZMQ.REQ).apply {
        connect("tcp://$host:$port")
    }

    override fun internalWrite(msg: String) {
        socket.send(msg, 0)
        socket.recv(0).let {
            messageReceived(String(it, ZMQ.CHARSET))
        }
    }

    override fun close() {
        super.close()
        socket.close()
        ctx.close()
    }

}