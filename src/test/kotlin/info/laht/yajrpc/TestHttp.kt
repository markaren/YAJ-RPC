package info.laht.yajrpc

import info.laht.yajrpc.net.RpcClient
import info.laht.yajrpc.net.RpcServer
import info.laht.yajrpc.net.http.RpcHttpClient
import info.laht.yajrpc.net.http.RpcHttpServer

class TestHttp : AbstractTestServer() {

    override fun createServer(): RpcServer {
        return RpcHttpServer(RpcHandler(service))
    }

    override fun createClient(port: Int): RpcClient {
        return RpcHttpClient("localhost", port)
    }

}