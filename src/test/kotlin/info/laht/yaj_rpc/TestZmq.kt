package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.AbstractRpcClient
import info.laht.yaj_rpc.net.RpcServer
import info.laht.yaj_rpc.net.zmq.RpcZmqClient
import info.laht.yaj_rpc.net.zmq.RpcZmqServer
import org.junit.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.ServerSocket

class TestZmq {

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(TestZmq::class.java)

        lateinit var server: RpcServer
        lateinit var client: AbstractRpcClient

        @JvmStatic
        @BeforeClass
        fun setup() {

            server = RpcZmqServer(RpcHandler(SampleService()))
            val port = server.start()

            client = RpcZmqClient("localhost", port)

        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            client.close()
            server.stop()
        }

    }

    @Test
    fun test1() {

        client.write("SampleService.greet", RpcListParams("Clint Eastwood")).also {
            LOG.info("Synchronous response=${it.getResult(String::class.java)}")
        }

    }


}