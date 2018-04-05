package info.laht.yaj_rpc

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestService {

    companion object {

        val LOG: Logger = LoggerFactory.getLogger(TestService::class.java)

        val gson: Gson = GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create()

    }

    @Test
    fun testService() {

        val handler = RpcHandler(SampleService())

        LOG.info(handler.getOpenMessage())

        LOG.info("${gson.fromJson(handler.handle(json1), Map::class.java)}")
        LOG.info("${gson.fromJson(handler.handle(json2), Map::class.java)}")
        LOG.info("${gson.fromJson(handler.handle(json3), Map::class.java)}")

    }

    private val json1 = """
            {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "SampleService.doubleInput",
                "params": [10]
            }
            """

    private val json2 = """
            {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "SampleService.complex",
                "params": [{"i": 1, "d": 2.0, "s": "per"}]
            }
            """

    private val json3 = """
            {
                "jsonrpc": "2.0",
                "id": 5,
                "method": "SampleService.returnNothing",
                "params": null
            }
            """
}
