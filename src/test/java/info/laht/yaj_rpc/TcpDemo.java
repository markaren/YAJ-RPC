package info.laht.yaj_rpc;

import info.laht.yaj_rpc.net.AbstractAsyncRpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.tcp.RpcTcpClient;
import info.laht.yaj_rpc.net.tcp.RpcTcpServer;
import kotlin.Unit;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TcpDemo {

    public static void main(String[] args) throws IOException {

        RpcHandler handler = new RpcHandler(new SampleService());

        int port = PortFinder.availablePort();
        RpcServer server = new RpcTcpServer(handler);
        server.start(port);

        AbstractAsyncRpcClient client = new RpcTcpClient("localhost", port);

        client.notify("SampleService.returnNothing", RpcParams.noParams());

        RpcParams params = RpcParams.listParams("Clint Eastwood");
        RpcResponse response = client.write("SampleService.greet", params);
        String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
        System.out.println("Response=" + result);

        client.writeAsync("SampleService.greet", params, res -> {
            System.out.println(res.getResult(String.class)); //prints 'Hello Client Eastwood!'
            return Unit.INSTANCE;
        });

        System.out.println("Press any key to exit..");
        Scanner sc = new Scanner(System.in);
        if (sc.hasNext()) {
            System.out.println("exiting..");
        }

        client.close();
        server.close();

    }

}
