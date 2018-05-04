package info.laht.yajrpc;


import info.laht.yajrpc.net.RpcClient;
import info.laht.yajrpc.net.RpcServer;
import info.laht.yajrpc.net.tcp.RpcTcpClient;
import info.laht.yajrpc.net.tcp.RpcTcpServer;

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
