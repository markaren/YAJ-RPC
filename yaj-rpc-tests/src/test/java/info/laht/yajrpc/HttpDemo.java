package info.laht.yajrpc;

import info.laht.yajrpc.net.AbstractRpcClient;
import info.laht.yajrpc.net.RpcServer;
import info.laht.yajrpc.net.http.RpcHttpClient;
import info.laht.yajrpc.net.http.RpcHttpServer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class HttpDemo {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        RpcHandler handler = new RpcHandler(new SampleService());
        RpcServer server = new RpcHttpServer(handler);
        int port = server.start();

        AbstractRpcClient client = new RpcHttpClient("localhost", port);

        DemoBase.run(server, client);

    }

}
