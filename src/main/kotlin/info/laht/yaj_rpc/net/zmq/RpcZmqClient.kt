package info.laht.yaj_rpc.net.zmq

import info.laht.yaj_rpc.net.AbstractRpcClient
import org.zeromq.ZMQ

class RpcZmqClient(
        private val host: String,
        private val port: Int
): AbstractRpcClient() {

    private val ctx = ZMQ.context(1)
    private val socket = ctx.socket(ZMQ.REQ).apply {
        connect("tcp://$host:$port")
    }

    override fun write(msg: String): String {
        socket.send(msg, 0)
        return socket.recv(0).let { String(it, ZMQ.CHARSET) }
    }

    override fun close() {
        socket.close()
        ctx.term()
    }


}