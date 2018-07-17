package info.laht.yajrpc;

import info.laht.yajrpc.net.RpcClient;
import info.laht.yajrpc.net.RpcServer;
import kotlin.Unit;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class DemoBase {

    static void run(RpcServer server, RpcClient client) throws IOException, TimeoutException, InterruptedException {

        try {

            client.notify("SampleService.returnNothing", RpcParams.noParams());

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
