package info.laht.yajrpc

import info.laht.yajrpc.net.RpcClient
import info.laht.yajrpc.net.RpcServer
import info.laht.yajrpc.net.zmq.RpcZmqClient
import info.laht.yajrpc.net.zmq.RpcZmqServer

class TestZmq : AbstractTestServer() {

    override fun createServer(): RpcServer {
        return RpcZmqServer(RpcHandler(service))
    }

    override fun createClient(port: Int): RpcClient {
        return RpcZmqClient("localhost", port)
    }

}