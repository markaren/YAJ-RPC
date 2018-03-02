package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.AbstractRpcClient
import info.laht.yaj_rpc.net.RpcServer
import info.laht.yaj_rpc.net.http.RpcHttpClient
import info.laht.yaj_rpc.net.http.RpcHttpServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.ServerSocket

class TestHttp {

    lateinit var server: RpcServer
    lateinit var client: AbstractRpcClient

    @Before
    fun setup() {

        val port = ServerSocket(0).use { it.localPort }
        server = RpcHttpServer(RpcHandler(SampleService())).also {
            it.start(port)
        }

        client = RpcHttpClient("localhost", port)

    }

    @After
    fun tearDown() {
        client.close()
        server.close()
    }

    @Test
    fun test1() {

        client.write("SampleService.greet", RpcParams.mapParams("name" to "Clint Eastwood")).also {
            LOG.info("Synchronous response=${it.getResult(String::class.java)}")
        }

    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(TestHttp::class.java)
    }

}