package info.laht.yaj_rpc;

import info.laht.yaj_rpc.net.AbstractRpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.tcp.RpcTcpClient;
import info.laht.yaj_rpc.net.tcp.RpcTcpServer;

public class TcpDemo {

    public static void main(String[] args) throws Exception {

        RpcHandler handler = new RpcHandler(
                new SampleService()
        );

        int port = 9777;
        RpcServer server = new RpcTcpServer(handler);
        server.start(port);

        AbstractRpcClient client = new RpcTcpClient("localhost", port);

        RpcParams params = RpcParams.listParams("Clint Eastwood");
        RpcResponse response = client.write("SampleService.greet", params);
        String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
        System.out.println(result);

        client.close();
        server.close();

    }

}
