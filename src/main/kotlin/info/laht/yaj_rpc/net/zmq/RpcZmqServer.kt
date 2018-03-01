package info.laht.yaj_rpc.net.zmq

import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.net.RpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zeromq.ZMQ

open class RpcZmqServer(
        private val handler: RpcHandler
): RpcServer {

    private lateinit var ctx: ZMQ.Context
    private lateinit var socket: ZMQ.Socket

    override fun start(port: Int) {

        ctx = ZMQ.context(1)
        socket = ctx.socket(ZMQ.REP).apply {
            bind("tcp://*:$port")
        }
        LOG.info("${javaClass.simpleName} listening for connections on port: $port")
        Thread {

            try {
                while (true) {
                    val recv = socket.recv(0).let {
                        String(it, ZMQ.CHARSET)
                    }

                    handler.handle(recv)?.also {
                        socket.send(it, 0)
                    } ?: socket.send("", 0)

                }
            } catch (ex: Exception) {
                LOG.debug("Caught exception", ex)
            }
        }.start()

    }

    override fun stop() {
        if (::ctx.isInitialized && ::socket.isInitialized) {
            socket.close()
            ctx.term()
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcZmqServer::class.java)
    }

}