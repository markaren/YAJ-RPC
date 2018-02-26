package info.laht.yaj_rpc;

import info.laht.yaj_rpc.net.ws.RpcWebSocketClient;
import info.laht.yaj_rpc.net.ws.RpcWebSocketServer;

class WebSocketDemo {

    public static void main(String[] args) {

        RpcHandler handler = new RpcHandler(
                new SampleService()
        );


        int port = 9777;
        RpcWebSocketServer server = new RpcWebSocketServer(handler);
        server.start(port);

        RpcWebSocketClient client = new RpcWebSocketClient("localhost", port);

        RpcParams params = RpcParams.listParams("Clint Eastwood");
        RpcResponse response = client.write("SampleService.greet", params);
        String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
        System.out.println(result);

        client.close();
        server.close();

    }

}
