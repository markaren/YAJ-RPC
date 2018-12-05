package info.laht.yajrpc

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestService {

    private companion object {
        val LOG: Logger = LoggerFactory.getLogger(TestService::class.java)
        val service = SampleService()
        val handler = RpcHandler(service)
    }

    @Test
    fun testDoubleInteger() {
        val request = formatMsg(1, "SampleService.doubleInteger", "[10]")
        val response = handler.handle(request)!!

        LOG.debug("JSON request=$request")
        LOG.debug("JSON response=$response")

        YAJRPC.fromJson<RpcResponse>(response).also {
            Assertions.assertEquals(20, it.getResult<Int>()!!)
        }
    }

    @Test
    fun testDoubleDouble() {
        val request = formatMsg(2.0, "SampleService.doubleDouble", "[10.25]")
        val response = handler.handle(request)!!

        LOG.debug("JSON request=$request")
        LOG.debug("JSON response=$response")

        YAJRPC.fromJson<RpcResponse>(response).also {
            Assertions.assertEquals(20.50, it.getResult<Double>()!!)
        }
    }

    @Test
    fun testComplex() {
        val request = formatMsg(3, "SampleService.complex", "[{\"i\": 1, \"d\": 2.0, \"s\": \"per\"}]")
        val response = handler.handle(request)!!

        LOG.debug("JSON request=$request")
        LOG.debug("JSON response=$response")

        YAJRPC.fromJson<RpcResponse>(response).also {
            it.getResult<SampleService.MyClass>()!!.also { result ->
                Assertions.assertEquals(1, result.i)
                Assertions.assertEquals(4.0, result.d)
                Assertions.assertEquals("per", result.s)
            }
        }
    }

    @Test
    fun testReturnNothing() {
        val request = formatMsg("4.0", "SampleService.returnNothing", "null")
        val response = handler.handle(request)!!

        LOG.debug("JSON request=$request")
        LOG.debug("JSON response=$response")

        YAJRPC.fromJson<RpcResponse>(response).also {
            Assertions.assertNull(it.getResult())
        }
    }

    @Test
    fun testGetSomeStrings() {
        val request = formatMsg(5, "SampleService.getSomeStrings", "null")
        val response = handler.handle(request)!!

        LOG.debug("JSON request=$request")
        LOG.debug("JSON response=$response")

        YAJRPC.fromJson<RpcResponse>(response).also {
            Assertions.assertEquals(service.someStrings, it.getResult<List<String>>())
        }
    }

    @Test
    fun testMultipleServices() {

        val handler = RpcHandler(SampleService("Service1"), SampleService("Service2"))

        val msg1 = formatMsg(1, "Service1.doubleInteger", "[10]")
        handler.handle(msg1).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
            LOG.info("$it")
            Assertions.assertEquals(20, it.getResult<Int>())
        }

        val json = formatMsg(1, "doubleInteger", "[10]")
        handler.handle(json).let { YAJRPC.fromJson<RpcResponse>(it!!) }.also {
            LOG.info("$it")
            Assertions.assertTrue(it.hasError)
        }

    }

    private fun formatMsg(id: Any, methodName: String, params: String): String {
        return """
            {
                "$JSON_RPC_IDENTIFIER": "2.0",
                "$ID_KEY": $id,
                "$METHOD_KEY": $methodName,
                "$PARAMS_KEY": $params
            }
            """
    }

}
