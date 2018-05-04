package info.laht.yajrpc

import info.laht.yajrpc.net.tcp.RpcTcpClient
import info.laht.yajrpc.net.tcp.RpcTcpServer
import org.junit.BeforeClass
import org.junit.Test

class TestTcp : AbstractTestServer() {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setup() {

            service = SampleService()
            server = RpcTcpServer(RpcHandler(service))
            val port = server.start()

            client = RpcTcpClient("localhost", port)

        }

    }

    @Test
    fun test() {
        run()
    }

}