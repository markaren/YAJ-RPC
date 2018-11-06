package info.laht.yajrpc

import info.laht.yajrpc.net.RpcClient
import info.laht.yajrpc.net.RpcServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractTestServer {

    private companion object {
        private val LOG: Logger = LoggerFactory.getLogger(AbstractTestServer::class.java)
    }

    val server: RpcServer
    val client: RpcClient
    val service: SampleService

    init {
        service = SampleService()
        server = createServer()
        client = createClient(server.start())
    }

    @AfterAll
    fun tearDown() {
        client.close()
        server.stop()
    }

    abstract fun createServer(): RpcServer
    abstract fun createClient(port: Int): RpcClient

    @Test
    fun run() {

        client.notify("SampleService.returnNothing")
        Thread.sleep(100)
        Assertions.assertTrue(service.returnNothingCalled)

        val latch = CountDownLatch(1)
        client.writeAsync("SampleService.greet", RpcParams.listParams("Clint Eastwood")) {
            LOG.info("Async response=${it.getResult(String::class.java)}")
            latch.countDown()
        }
        latch.await(1000, TimeUnit.MILLISECONDS)

        client.write("SampleService.greet", RpcListParams("Clint Eastwood")).also {
            LOG.info("Synchronous response=${it.getResult(String::class.java)}")
        }

        testWrapper()
    }

    fun testWrapper(){
        val wrapper = SampleServiceWrapper(client)

        wrapper.returnNothing()
        Assertions.assertEquals(wrapper.greet("Clint Eastwood"), service.greet("Clint Eastwood"))

        val clazz = SampleService.MyClass().apply {
            i = 1
            d = 2.0
            s = "foo"
        }
        Assertions.assertEquals(wrapper.complex(clazz).d, 4.0)

    }

}