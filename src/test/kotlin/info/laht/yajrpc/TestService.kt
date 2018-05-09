package info.laht.yajrpc

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestService {

    companion object {

        private val LOG: Logger = LoggerFactory.getLogger(TestService::class.java)

        private val gson: Gson = GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create()

    }

    @Test
    fun testService() {

        val handler = RpcHandler(SampleService())

        LOG.info(handler.getOpenMessage())

        handler.handle(json1).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
            LOG.info("$it")
            Assert.assertEquals(20.0, it.getResult<Double>()!!, 0.0)
        }

        handler.handle(json2).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
            LOG.info("$it")
            val result = it.getResult<SampleService.MyClass>()!!
            Assert.assertEquals(1, result.i)
            Assert.assertEquals(4.0, result.d, 0.0)
            Assert.assertEquals("per", result.s)
        }

        handler.handle(json3).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
            LOG.info("$it")
            Assert.assertNull(it.getResult())
        }


    }

    private val json1 = """
            {
                "$JSON_RPC_IDENTIFIER": "2.0",
                "$ID_KEY": 1,
                "$METHOD_KEY": "SampleService.doubleInput",
                "$PARAMS_KEY": [10]
            }
            """

    private val json2 = """
            {
                "$JSON_RPC_IDENTIFIER": "2.0",
                "$ID_KEY": 1,
                "$METHOD_KEY": "SampleService.complex",
                "$PARAMS_KEY": [{"i": 1, "d": 2.0, "s": "per"}]
            }
            """

    private val json3 = """
            {
                "$JSON_RPC_IDENTIFIER": "2.0",
                "$ID_KEY": 5,
                "$METHOD_KEY": "SampleService.returnNothing",
                "$PARAMS_KEY": null
            }
            """
}
