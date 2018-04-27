package info.laht.yajrpc

import info.laht.yajrpc.net.http.RpcHttpClient
import info.laht.yajrpc.net.http.RpcHttpServer
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

    }

    @Test
    fun test() {
        run()
    }


}