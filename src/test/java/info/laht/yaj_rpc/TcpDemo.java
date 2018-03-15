package info.laht.yaj_rpc;


import info.laht.yaj_rpc.net.RpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.tcp.RpcTcpClient;
import info.laht.yaj_rpc.net.tcp.RpcTcpServer;
import kotlin.Unit;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class TcpDemo {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        RpcHandler handler = new RpcHandler(new SampleService());

        RpcServer server = new RpcTcpServer(handler);
        int port = server.start();

        RpcClient client = new RpcTcpClient("localhost", port);

        try {

            client.notify("SampleService.returnNothing");

            RpcParams params = RpcParams.listParams("Clint Eastwood");
            RpcResponse response = client.write("SampleService.greet", params);
            String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
            System.out.println("Response=" + result);

            client.writeAsync("SampleService.greet", params, res -> {
                System.out.println(res.getResult(String.class)); //prints 'Hello Client Eastwood!'
                return Unit.INSTANCE;
            });

            Thread.sleep(100);

            System.out.println("Press any key to exit..");
            Scanner sc = new Scanner(System.in);
            if (sc.hasNext()) {
                System.out.println("exiting..");
            }

        } finally {

            client.close();
            server.close();

        }

    }

}
