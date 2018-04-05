package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.zmq.RpcZmqClient
import info.laht.yaj_rpc.net.zmq.RpcZmqServer
import org.junit.BeforeClass
import org.junit.Test

class TestZmq : AbstractTestServer() {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setup() {

            service = SampleService()
            server = RpcZmqServer(RpcHandler(service))
            val port = server.start()

            client = RpcZmqClient("localhost", port)

        }

    }

    @Test
    fun test() {
        run()
    }

}