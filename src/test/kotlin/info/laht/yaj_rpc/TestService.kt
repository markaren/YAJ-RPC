package info.laht.yaj_rpc

import com.google.gson.GsonBuilder
import org.junit.Test
import org.slf4j.LoggerFactory

class TestService {

    companion object {
        val LOG = LoggerFactory.getLogger(TestService::class.java)
    }

    val gson = GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()

    @Test
    fun testService() {

        val handler = RpcHandler(SampleService())

        LOG.info(handler.getOpenMessage())

        LOG.info("${gson.fromJson(handler.handle(json1), Map::class.java)}")
        LOG.info("${gson.fromJson(handler.handle(json2), Map::class.java)}")
        LOG.info("${gson.fromJson(handler.handle(json3), Map::class.java)}")

    }

    val json1 = """
            {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "SampleService.doubleInput",
                "params": [10]
            }
            """

    val json2 = """
            {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "SampleService.complex",
                "params": [{"i": 1, "d": 2.0, "s": "per"}]
            }
            """

    val json3 = """
            {
                "jsonrpc": "2.0",
                "id": 5,
                "method": "SampleService.returnNothing",
                "params": null
            }
            """
}
