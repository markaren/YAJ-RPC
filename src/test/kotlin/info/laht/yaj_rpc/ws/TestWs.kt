package info.laht.yaj_rpc.ws

import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.RpcListParams
import info.laht.yaj_rpc.RpcParams
import info.laht.yaj_rpc.SampleService
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TestWs {

    lateinit var server: RpcWebSocketServer
    lateinit var client: RpcWebSocketClient

    @Before
    fun setup() {

        val handler = RpcHandler().apply {
            addService(SampleService())
        }

        val port = 9777

        server = RpcWebSocketServer(port, handler).also {
            it.start()
        }

        client = RpcWebSocketClient("localhost", port).also {
            it.connect()
        }


    }

    @After
    fun tearDown() {
        client.close()
        server.stop()
    }

    @Test
    fun test1() {

        if (false) {
            val latch = CountDownLatch(1)
            client.writeAsync("SampleService.greet", RpcParams.listParams("per"), {
                println(it.getResult(String::class.java))
                latch.countDown()
            })
            latch.await(1000, TimeUnit.MILLISECONDS)
        } else {

            println(client.write("SampleService.greet", RpcListParams("per")).getResult(String::class.java))

        }

    }


}