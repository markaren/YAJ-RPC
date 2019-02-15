
package info.laht.yajrpc.net.ws

import info.laht.yajrpc.RpcHandler
import info.laht.yajrpc.RpcMethod
import info.laht.yajrpc.RpcService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class TestWs {


    @Test
    fun test() {

        val port = 9091

        for (i in 0..4) {

            val server = RpcWebSocketServer(RpcHandler(DummyService(i))).apply {
                start(port)
            }
            RpcWebSocketClient("localhost", port).use {
                it.write("sayHello").get().also { response ->
                   Assertions.assertEquals("hello_$i", response.getResult<String>())
                }
            }

            server.stop()

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
