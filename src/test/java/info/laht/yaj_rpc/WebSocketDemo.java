package info.laht.yaj_rpc;

import info.laht.yaj_rpc.net.RpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.ws.RpcWebSocketClient;
import info.laht.yaj_rpc.net.ws.RpcWebSocketServer;

class WebSocketDemo {

    public static void main(String[] args) throws Exception {

        RpcHandler handler = new RpcHandler(new SampleService());
        RpcServer server = new RpcWebSocketServer(handler);
        int port = server.start();

        RpcClient client = new RpcWebSocketClient("localhost", port);

        DemoBase.run(server, client);

    }

}
