package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.ws.RpcWebSocketClient
import info.laht.yaj_rpc.net.ws.RpcWebSocketServer
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