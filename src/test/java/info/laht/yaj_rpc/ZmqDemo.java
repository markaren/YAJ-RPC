package info.laht.yaj_rpc;


import info.laht.yaj_rpc.net.RpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.zmq.RpcZmqClient;
import info.laht.yaj_rpc.net.zmq.RpcZmqServer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class ZmqDemo {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        RpcHandler handler = new RpcHandler(new SampleService());
        RpcServer server = new RpcZmqServer( handler);
        int port = server.start();

        RpcClient client = new RpcZmqClient("localhost", port);

        DemoBase.run(server, client);

    }

}
