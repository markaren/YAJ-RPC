package info.laht.yaj_rpc;


import info.laht.yaj_rpc.net.RpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.tcp.RpcTcpClient;
import info.laht.yaj_rpc.net.tcp.RpcTcpServer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class TcpDemo {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        RpcHandler handler = new RpcHandler(new SampleService());
        RpcServer server = new RpcTcpServer(handler);
        int port = server.start();

        RpcClient client = new RpcTcpClient("localhost", port);

        DemoBase.run(server, client);

    }

}
