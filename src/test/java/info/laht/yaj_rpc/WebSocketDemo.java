package info.laht.yaj_rpc;

import info.laht.yaj_rpc.RpcHandler;
import info.laht.yaj_rpc.ws.RpcWebSocketClient;
import info.laht.yaj_rpc.ws.RpcWebSocketServer;
import info.laht.yaj_rpc.RpcParams;
import info.laht.yaj_rpc.RpcResponse;

class WebSocketDemo {

    public static void main(String[] args) {

        RpcHandler handler = new RpcHandler();
        handler.addService(new SampleService());

        int port = 9777;
        RpcWebSocketServer server = new RpcWebSocketServer(port, handler);
        server.start();

        RpcWebSocketClient client = new RpcWebSocketClient("localhost", port);
        client.connect();

        RpcParams params = RpcParams.listParams("Clint Eastwood");
        RpcResponse response = client.write("SampleService.greet", params);
        String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
        System.out.println(result);

        client.close();
        server.close();

    }

}
