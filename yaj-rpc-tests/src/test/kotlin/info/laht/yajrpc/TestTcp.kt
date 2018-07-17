package info.laht.yajrpc

import info.laht.yajrpc.net.RpcClient
import info.laht.yajrpc.net.RpcServer
import info.laht.yajrpc.net.tcp.RpcTcpClient
import info.laht.yajrpc.net.tcp.RpcTcpServer

class TestTcp : AbstractTestServer() {

    override fun createServer(): RpcServer {
        return RpcTcpServer(RpcHandler(service))
    }

    override fun createClient(port: Int): RpcClient {
        return RpcTcpClient("localhost", port)
    }

}