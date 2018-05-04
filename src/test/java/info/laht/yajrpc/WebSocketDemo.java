package info.laht.yajrpc;

import info.laht.yajrpc.net.RpcClient;
import info.laht.yajrpc.net.RpcServer;
import info.laht.yajrpc.net.ws.RpcWebSocketClient;
import info.laht.yajrpc.net.ws.RpcWebSocketServer;

class WebSocketDemo {

    public static void main(String[] args) throws Exception {

        RpcHandler handler = new RpcHandler(new SampleService());
        RpcServer server = new RpcWebSocketServer(handler);
        int port = server.start();

        RpcClient client = new RpcWebSocketClient("localhost", port);

        DemoBase.run(server, client);

    }

}
