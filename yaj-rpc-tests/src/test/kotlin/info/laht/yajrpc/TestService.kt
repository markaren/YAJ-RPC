package info.laht.yajrpc

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestService {

    private companion object {
        private val LOG: Logger = LoggerFactory.getLogger(TestService::class.java)
    }

    @Test
    fun testService() {

        RpcHandler(SampleService()).apply {

            LOG.info(getOpenMessage())

            handle(json1).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                Assertions.assertEquals(20.0, it.getResult<Double>()!!)
            }

            handle(json2).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                val result = it.getResult<SampleService.MyClass>()!!
                Assertions.assertEquals(1, result.i)
                Assertions.assertEquals(4.0, result.d)
                Assertions.assertEquals("per", result.s)
            }

            handle(json3).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                Assertions.assertNull(it.getResult())
            }

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
