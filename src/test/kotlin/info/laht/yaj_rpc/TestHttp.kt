package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.AbstractRpcClient
import info.laht.yaj_rpc.net.RpcServer
import info.laht.yaj_rpc.net.http.RpcHttpClient
import info.laht.yaj_rpc.net.http.RpcHttpServer
import org.junit.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.ServerSocket

class TestHttp {

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(TestHttp::class.java)

        lateinit var server: RpcServer
        lateinit var client: AbstractRpcClient

        @JvmStatic
        @BeforeClass
        fun setup() {

            server = RpcHttpServer(RpcHandler(SampleService()))
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

        client.write("SampleService.greet", RpcParams.mapParams("name" to "Clint Eastwood")).also {
            LOG.info("Synchronous response=${it.getResult(String::class.java)}")
        }

    }


}