package info.laht.yaj_rpc;

import info.laht.yaj_rpc.net.AbstractAsyncRpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.tcp.RpcTcpClient;
import info.laht.yaj_rpc.net.tcp.RpcTcpServer;
import kotlin.Unit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TcpDemo {

    public static void main(String[] args) throws Exception {

        RpcHandler handler = new RpcHandler(new SampleService());

        int port = PortFinder.availablePort();
        RpcServer server = new RpcTcpServer(handler);
        server.start(port);

        AbstractAsyncRpcClient client = new RpcTcpClient("localhost", port);

        RpcParams params = RpcParams.listParams("Clint Eastwood");

        RpcResponse response = client.write("SampleService.greet", params);
        String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
        System.out.println(result);

        CountDownLatch latch = new CountDownLatch(1);
        client.writeAsync("SampleService.greet", params, res -> {
            System.out.println(res.getResult(String.class));
            latch.countDown();
            return Unit.INSTANCE;
        });
        latch.await(1000, TimeUnit.MILLISECONDS);


        client.close();
        server.close();

    }

}
