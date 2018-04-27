package info.laht.yajrpc

import info.laht.yajrpc.net.ws.RpcWebSocketClient
import info.laht.yajrpc.net.ws.RpcWebSocketServer
import org.junit.BeforeClass
import org.junit.Test

class TestWs : AbstractTestServer() {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setup() {

            service = SampleService()
            server = RpcWebSocketServer(RpcHandler(service))
            val port = server.start()

            client = RpcWebSocketClient("localhost", port)

        }

    }

    @Test
    fun test() {
        run()
    }


}