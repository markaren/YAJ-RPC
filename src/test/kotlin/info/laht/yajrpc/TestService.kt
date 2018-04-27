package info.laht.yajrpc

import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

        LOG.info("${gson.fromJson(handler.handle(json1), Map::class.java)}")
        LOG.info("${gson.fromJson(handler.handle(json2), Map::class.java)}")
        LOG.info("${gson.fromJson(handler.handle(json3), Map::class.java)}")

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
