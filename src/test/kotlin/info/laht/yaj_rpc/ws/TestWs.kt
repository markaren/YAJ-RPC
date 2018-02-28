package info.laht.yaj_rpc.ws

import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.RpcListParams
import info.laht.yaj_rpc.RpcParams
import info.laht.yaj_rpc.SampleService
import info.laht.yaj_rpc.net.AbstractAsyncRpcClient
import info.laht.yaj_rpc.net.RpcServer
import info.laht.yaj_rpc.net.ws.RpcWebSocketClient
import info.laht.yaj_rpc.net.ws.RpcWebSocketServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.ServerSocket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TestWs {

    lateinit var server: RpcServer
    lateinit var client: AbstractAsyncRpcClient

    @Before
    fun setup() {

        val port = ServerSocket(0).use { it.localPort }
        server = RpcWebSocketServer(RpcHandler(SampleService())).also {
            it.start(port)
        }

        client = RpcWebSocketClient("localhost", port)

    }

    @After
    fun tearDown() {
        client.close()
        server.stop()
    }

    @Test
    fun test1() {


        val latch = CountDownLatch(1)
        client.writeAsync("SampleService.greet", RpcParams.listParams("per"), {
            println("async response=${it.getResult(String::class.java)}")
            latch.countDown()
        })
        latch.await(1000, TimeUnit.MILLISECONDS)

        client.write("SampleService.greet", RpcListParams("per")).also {
            println("syncronous response=${it.getResult(String::class.java)}")
        }

    }


}