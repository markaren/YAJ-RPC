package info.laht.yajrpc;


import info.laht.yajrpc.net.RpcClient;
import info.laht.yajrpc.net.RpcServer;
import info.laht.yajrpc.net.zmq.RpcZmqClient;
import info.laht.yajrpc.net.zmq.RpcZmqServer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class ZmqDemo {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        RpcHandler handler = new RpcHandler(new SampleService());
        RpcServer server = new RpcZmqServer(handler);
        int port = server.start();

        RpcClient client = new RpcZmqClient("localhost", port);

        DemoBase.run(server, client);

    }

}
