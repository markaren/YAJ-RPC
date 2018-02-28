package info.laht.yaj_rpc;

import info.laht.yaj_rpc.net.AbstractAsyncRpcClient;
import info.laht.yaj_rpc.net.AbstractRpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.http.RpcHttpClient;
import info.laht.yaj_rpc.net.http.RpcHttpServer;

public class HttpDemo {


    public static void main(String[] args) throws Exception {

        RpcHandler handler = new RpcHandler(new SampleService());

        int port = PortFinder.availablePort();
        RpcServer server = new RpcHttpServer(handler);
        server.start(port);

        AbstractRpcClient client = new RpcHttpClient("localhost", port);
        RpcParams params = RpcParams.listParams("Clint Eastwood");

        RpcResponse response = client.write("SampleService.greet", params);
        String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
        System.out.println(result);

        client.close();
        server.close();

    }

}
