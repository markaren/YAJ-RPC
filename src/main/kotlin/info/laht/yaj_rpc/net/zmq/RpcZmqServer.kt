package info.laht.yaj_rpc.net.zmq

import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.net.RpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zeromq.ZMQ

open class RpcZmqServer(
        private val handler: RpcHandler
): RpcServer {

    override var port: Int? = null

    private var ctx: ZMQ.Context? = null
    private var socket: ZMQ.Socket? = null


    override fun start(port: Int) {

        if (socket == null) {

            this.port = port
            ctx = ZMQ.context(1)
            socket = ctx!!.socket(ZMQ.REP).also {socket ->

                socket.bind("tcp://*:$port")

                Thread {

                    try {
                        while (true) {

                            val received = String(socket.recv(0), ZMQ.CHARSET)
                            LOG.trace(received)

                            handler.handle(received)?.also {
                                socket.send(it, 0)
                            } ?: socket.send("", 0)

                        }
                    } catch (ex: Exception) {
                        LOG.trace("Caught exception", ex)
                    }

                    LOG.debug("${javaClass.simpleName} stopped!")

                }.start()

                LOG.info("${javaClass.simpleName} listening for connections on port: $port")

            }

        } else {
            LOG.warn("${javaClass.simpleName} is already running!")
        }

    }

    override fun stop() {
        socket?.apply {
            close()
            socket = null
        }
        ctx?.apply {
            term()
            ctx = null
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcZmqServer::class.java)
    }

}