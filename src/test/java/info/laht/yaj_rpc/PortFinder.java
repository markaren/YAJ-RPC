package info.laht.yaj_rpc;

import java.io.IOException;
import java.net.ServerSocket;

public class PortFinder {

    static int availablePort() throws IOException {

        try(ServerSocket ss = new ServerSocket(0)) {
            return ss.getLocalPort();
        }

    }

}
