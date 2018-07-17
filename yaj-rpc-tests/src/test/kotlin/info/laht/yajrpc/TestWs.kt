//package info.laht.yajrpc
//
//import info.laht.yajrpc.net.RpcClient
//import info.laht.yajrpc.net.RpcServer
//import info.laht.yajrpc.net.ws.RpcWebSocketClient
//import info.laht.yajrpc.net.ws.RpcWebSocketServer
//
//class TestWs : AbstractTestServer() {
//
//    override fun createServer(): RpcServer {
//        return RpcWebSocketServer(RpcHandler(service))
//    }
//
//    override fun createClient(port: Int): RpcClient {
//        return RpcWebSocketClient("localhost", port)
//    }
//
//}