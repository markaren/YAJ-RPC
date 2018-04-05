package info.laht.yaj_rpc

import info.laht.yaj_rpc.net.RpcClient
import info.laht.yaj_rpc.net.RpcServer
import org.junit.AfterClass
import org.junit.Assert
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

abstract class AbstractTestServer {

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(AbstractTestServer::class.java)

        lateinit var server: RpcServer
        lateinit var service: SampleService
        lateinit var client: RpcClient

        @JvmStatic
        @AfterClass
        fun tearDown() {
            client.close()
            server.stop()
        }

    }

    fun run() {

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