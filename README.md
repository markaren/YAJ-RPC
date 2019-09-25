# YAJ-RPC

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/markaren/YAJ-RPC/issues)

[![](https://jitpack.io/v/markaren/YAJ-RPC.svg)](https://jitpack.io/#markaren/YAJ-RPC) 
[![Join the chat at https://gitter.im/markaren/YAJ-RPC](https://badges.gitter.im/markaren/YAJ-RPC.svg)](https://gitter.im/markaren/YAJ-RPC?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


[![CircleCI](https://circleci.com/gh/markaren/YAJ-RPC/tree/master.svg?style=svg)](https://circleci.com/gh/markaren/YAJ-RPC/tree/master) 


Yet Another JSON-RPC (YAJ-RPC) is a [JSON RPC](https://www.jsonrpc.org/specification) 2.0 implementation for JVM languages written in [Kotlin](https://kotlinlang.org/).


Client and server for WebSockets, TCP/IP ZeroMQ and HTTP are available, 
but the RPC implementations itself is totally independent from any networking logic.

---

Due to how cumbersome it is to publish artifacts to Maven Central, new releases can only be obtained using __jitpack__.


```groovy
allprojects {
    repositories {
        /*...*/
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    def yajrpc_version = "..."
    implementation group: 'com.github.markaren.YAJ-RPC', name: 'yaj-rpc', version: yajrpc_version
    implementation group: 'com.github.markaren.YAJ-RPC', name: 'yaj-rpc-ws', version: yajrpc_version
    implementation group: 'com.github.markaren.YAJ-RPC', name: 'yaj-rpc-tcp', version: yajrpc_version
    implementation group: 'com.github.markaren.YAJ-RPC', name: 'yaj-rpc-zmq', version: yajrpc_version
    implementation group: 'com.github.markaren.YAJ-RPC', name: 'yaj-rpc-http', version: yajrpc_version
}
```

To get started head over to the [Wiki](https://github.com/markaren/YAJ-RPC/wiki)!
