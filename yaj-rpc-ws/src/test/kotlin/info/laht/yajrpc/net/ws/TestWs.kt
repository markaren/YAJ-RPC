
package info.laht.yajrpc.net.ws

import info.laht.yajrpc.RpcHandler
import info.laht.yajrpc.RpcMethod
import info.laht.yajrpc.RpcService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration


class TestWs {

    @Test
    fun test1() {

        for (i in 0..5) {

            Assertions.assertTimeout(Duration.ofSeconds(2)) {
                RpcWebSocketServer(RpcHandler(DummyService(i))).use { server ->
                    RpcWebSocketClient("localhost",  server.start()).use { client ->
                        client.write("sayHello").get().also { response ->
                            Assertions.assertEquals("hello_$i", response.getResult<String>())
                        }
                    }
                }
            }

        }

    }

}


class DummyService(
        private val i: Int
): RpcService {

    override val serviceName: String
        get() = DummyService::class.java.simpleName

    @RpcMethod
    fun sayHello(): String {
        return "hello_$i"
    }

}
