package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.http.RpcHttpClient
import info.laht.yaj_rpc.net.http.RpcHttpServer
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class TestHttp : AbstractTestServer() {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setup() {

            service = SampleService()
            server = RpcHttpServer(RpcHandler(service))
            val port = server.start()

            client = RpcHttpClient("localhost", port)

        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            client.close()
            server.close()
        }

    }

    @Test
    fun test1() {

        run()

    }


}