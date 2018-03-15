package info.laht.yaj_rpc;


import info.laht.yaj_rpc.net.RpcClient;
import info.laht.yaj_rpc.net.RpcServer;
import info.laht.yaj_rpc.net.zmq.RpcZmqClient;
import info.laht.yaj_rpc.net.zmq.RpcZmqServer;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ZmqTest {

    public static void main(String[] args) throws IOException, TimeoutException {
        RpcHandler handler = new RpcHandler(new SampleService());

        RpcServer server = new RpcZmqServer( handler);
        int port = server.start();

        RpcClient client = new RpcZmqClient("localhost", port);

        try {

            client.notify("SampleService.returnNothing");

            RpcParams params = RpcParams.listParams("Clint Eastwood");
            RpcResponse response = client.write("SampleService.greet", params);
            String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
            System.out.println("Response=" + result);

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
