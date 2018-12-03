package info.laht.yajrpc

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestService {

    private companion object {
        private val LOG: Logger = LoggerFactory.getLogger(TestService::class.java)
    }

    private fun formatMsg(id: Int, methodName: String, params: String): String {
        return """
            {
                "$JSON_RPC_IDENTIFIER": "2.0",
                "$ID_KEY": $id,
                "$METHOD_KEY": $methodName,
                "$PARAMS_KEY": $params
            }
            """
    }

    @Test
    fun testService() {

        RpcHandler(SampleService()).apply {

            LOG.info(getOpenMessage())

            val json1 = formatMsg(1, "SampleService.doubleInteger", "[10]")
            val json2 = formatMsg(2, "SampleService.doubleDouble", "[10.25]")
            val json3 = formatMsg(3, "SampleService.complex", "[{\"i\": 1, \"d\": 2.0, \"s\": \"per\"}]")
            val json4 = formatMsg(4, "SampleService.returnNothing", "null")
            val json5 = formatMsg(5, "SampleService.getSomeStrings", "null")

            handle(json1).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                Assertions.assertEquals(20, it.getResult<Int>()!!)
            }

            handle(json2).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                Assertions.assertEquals(20.50, it.getResult<Double>()!!)
            }

            handle(json3).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                val result = it.getResult<SampleService.MyClass>()!!
                Assertions.assertEquals(1, result.i)
                Assertions.assertEquals(4.0, result.d)
                Assertions.assertEquals("per", result.s)
            }

            handle(json4).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                Assertions.assertNull(it.getResult())
            }

            handle(json5).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                Assertions.assertEquals(listOf("String1", "String2", "String3"), it.getResult<List<String>>())
            }

        }

    }

    @Test
    fun testServices() {

        RpcHandler(SampleService("Service1"), SampleService("Service2")).apply {

            val json = formatMsg(1, "doubleInput", "[10]")

            handle(json).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
                LOG.info("$it")
                Assertions.assertTrue(it.hasError)
            }

        }

    }


}
