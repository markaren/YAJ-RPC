package info.laht.yajrpc

import info.laht.yajrpc.net.RpcClient
import info.laht.yajrpc.net.RpcServer
import info.laht.yajrpc.net.http.RpcOkHttpClient
import info.laht.yajrpc.net.http.RpcHttpClient
import info.laht.yajrpc.net.http.RpcHttpServer
import info.laht.yajrpc.net.tcp.RpcTcpClient
import info.laht.yajrpc.net.tcp.RpcTcpServer
import info.laht.yajrpc.net.ws.RpcWebSocketClient
import info.laht.yajrpc.net.ws.RpcWebSocketServer
import info.laht.yajrpc.net.zmq.RpcZmqClient
import info.laht.yajrpc.net.zmq.RpcZmqServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractTestServer {

    private companion object {
        private val LOG: Logger = LoggerFactory.getLogger(AbstractTestServer::class.java)
    }

    private val server: RpcServer
    private val client: RpcClient
    private val service = SampleService()
    private val handler = RpcHandler(service)

    init {
        server = createServer(handler)
        client = createClient(server.start())
    }

    @AfterAll
    fun tearDown() {
        client.close()
        server.stop()
    }

    abstract fun createServer(handler: RpcHandler): RpcServer
    abstract fun createClient(port: Int): RpcClient

    @Test
    fun test1() {

        client.notify("SampleService.returnNothing")
        Thread.sleep(100)
        Assertions.assertTrue(service.returnNothingCalled)

        client.write("SampleService.greet", RpcListParams("Clint Eastwood")).get().also {
            LOG.info("Response=${it.getResult<String>()}")
        }

        client.write("greet", RpcListParams("Clint Eastwood")).get().also {
            LOG.info("Response=${it.getResult<String>()}")
        }

    }

    @Test
    fun test2() {

        client.write("complex", RpcListParams( SampleService.MyClass(1,2.0, "foo"))).get().also {

            val result = it.getResult<SampleService.MyClass>()!!
            Assertions.assertEquals(result.i,  1)
            Assertions.assertEquals(result.d, 4.0)
            Assertions.assertEquals(result.s, "foo")

            LOG.info("Response=$result")
        }

    }

    @Test
    private fun testWrapper(){
        val wrapper = SampleServiceWrapper(client)

        wrapper.returnNothing()
        Assertions.assertEquals(wrapper.greet("Clint Eastwood"), service.greet("Clint Eastwood"))

        val clazz = SampleService.MyClass(1,2.0, "foo")
        val result = wrapper.complex(clazz)
        Assertions.assertEquals(result.i,  1)
        Assertions.assertEquals(result.d, 4.0)
        Assertions.assertEquals(result.s, "foo")

    }

}

open class TestHttp : AbstractTestServer() {

    override fun createServer(handler: RpcHandler): RpcServer {
        return RpcHttpServer(handler)
    }

    override fun createClient(port: Int): RpcClient {
        return RpcHttpClient("localhost", port)
    }

}

class TestOkHttp : TestHttp() {

    override fun createClient(port: Int): RpcClient {
        return RpcOkHttpClient("localhost", port)
    }

}

class TestWs : AbstractTestServer() {

    override fun createServer(handler: RpcHandler): RpcServer {
        return RpcWebSocketServer(handler)
    }

    override fun createClient(port: Int): RpcClient {
        return RpcWebSocketClient("localhost", port)
    }

}

class TestZmq : AbstractTestServer() {

    override fun createServer(handler: RpcHandler): RpcServer {
        return RpcZmqServer(handler)
    }

    override fun createClient(port: Int): RpcClient {
        return RpcZmqClient("localhost", port)
    }

}

class TestTcp : AbstractTestServer() {

    override fun createServer(handler: RpcHandler): RpcServer {
        return RpcTcpServer(handler)
    }

    override fun createClient(port: Int): RpcClient {
        return RpcTcpClient("localhost", port)
    }

}