package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.AbstractRpcClient
import info.laht.yaj_rpc.net.RpcServer
import info.laht.yaj_rpc.net.zmq.RpcZmqClient
import info.laht.yaj_rpc.net.zmq.RpcZmqServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.ServerSocket

class TestZmq {

    lateinit var server: RpcServer
    lateinit var client: AbstractRpcClient

    @Before
    fun setup() {

        val port = ServerSocket(0).use { it.localPort }
        server = RpcZmqServer(RpcHandler(SampleService())).also {
            it.start(port)
        }

        client = RpcZmqClient("localhost", port)

    }

    @After
    fun tearDown() {
        client.close()
        server.stop()
    }

    @Test
    fun test1() {

        client.write("SampleService.greet", RpcListParams("per")).also {
            println("syncronous response=${it.getResult(String::class.java)}")
        }

    }
}