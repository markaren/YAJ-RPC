# YAJ-RPC

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/markaren/YAJ-RPC/issues)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/info.laht.YAJ-RPC/badge.svg)](https://mvnrepository.com/artifact/info.laht.YAJ-RPC)



Yet Another JSON RPC (YAJ-RPC) is a JSON RPC 2.0 implementation for JVM languages written in Kotlin.

Client and server for WebSockets are included, but the RPC implementations itself is totally independent from any networking logic.

RPC methods are regular methods annotated with @RpcMethod, like so:

### Example service
```java

import info.laht.yaj_rpc.RpcMethod;
import info.laht.yaj_rpc.AbstractRpcService;

class MyService implements RpcService {
    
    @Override
    public String getName() {
        return MyService.class.getSimpleName();
    }
    
    @RpcMethod
    public String greet(String input)  {
        return "Hello " + input + '!';
    }
    
}

```

In order to invoke the above 

```java

import info.laht.yaj_rpc.RpcHandler;
import info.laht.yaj_rpc.ws.RpcWebSocketClient;
import info.laht.yaj_rpc.ws.RpcWebSocketServer;
import info.laht.yaj_rpc.RpcParams;
import info.laht.yaj_rpc.RpcResponse;

class WebSocketDemo {
    
    public static void main(String[] args) {
        
        RpcHandler handler = new RpcHandler();
        handler.addService(new SampleService());

        int port = 9777;
        RpcWebSocketServer server = new RpcWebSocketServer(port, handler);
        server.start();

        RpcWebSocketClient client = new RpcWebSocketClient("localhost", port);
        client.connect();

        RpcParams params = RpcParams.listParams("Clint Eastwood");
        RpcResponse response = client.write("SampleService.greet", params);
        String result = response.getResult(String.class); //prints 'Hello Client Eastwood!'
        System.out.println(result);

        client.close();
        server.close();
        
    }
    
}

```