package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.RpcClient
import info.laht.yaj_rpc.net.RpcServer
import info.laht.yaj_rpc.net.tcp.RpcTcpClient
import info.laht.yaj_rpc.net.tcp.RpcTcpServer
import org.junit.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TestTcp {

   companion object {

       val LOG: Logger = LoggerFactory.getLogger(TestTcp::class.java)

       lateinit var server: RpcServer
       lateinit var service: SampleService
       lateinit var client: RpcClient

       @JvmStatic
       @BeforeClass
       fun setup() {

           service = SampleService()
           server = RpcTcpServer(RpcHandler(service))
           val port = server.start()

           client = RpcTcpClient("localhost", port)

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

        client.notify("SampleService.returnNothing")
        Thread.sleep(100)
        Assert.assertTrue(service.returnNothingCalled)

        val latch = CountDownLatch(1)
        client.writeAsync("SampleService.greet", RpcParams.listParams("Clint Eastwood"), {
            LOG.info("Async response=${it.getResult(String::class.java)}")
            latch.countDown()
        })
        latch.await(1000, TimeUnit.MILLISECONDS)

        client.write("SampleService.greet", RpcListParams("Clint Eastwood")).also {
            LOG.info("Synchronous response=${it.getResult(String::class.java)}")
        }

    }

}