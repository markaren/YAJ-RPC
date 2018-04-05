package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.tcp.RpcTcpClient
import info.laht.yaj_rpc.net.tcp.RpcTcpServer
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