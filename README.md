# YAJ-RPC

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/markaren/YAJ-RPC/issues)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/info.laht/yaj-rpc/badge.svg)](https://mvnrepository.com/artifact/info.laht/yaj-rpc)

[![CircleCI](https://circleci.com/gh/markaren/YAJ-RPC/tree/master.svg?style=svg)](https://circleci.com/gh/markaren/YAJ-RPC/tree/master) [![Join the chat at https://gitter.im/markaren/YAJ-RPC](https://badges.gitter.im/markaren/YAJ-RPC.svg)](https://gitter.im/markaren/YAJ-RPC?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Yet Another JSON-RPC (YAJ-RPC) is a JSON RPC 2.0 implementation for JVM languages written in Kotlin.


Client and server for WebSockets, TCP/IP ZeroMQ and HTTP are available, 
but the RPC implementations itself is totally independent from any networking logic.


```gradle
dependencies {
    def yajrpc_version = "..."
    implementation group: 'info.laht', name: 'yaj-rpc', version: yajrpc_version
    implementation group: 'info.laht', name: 'yaj-rpc-ws', version: yajrpc_version
    implementation group: 'info.laht', name: 'yaj-rpc-tcp', version: yajrpc_version
    implementation group: 'info.laht', name: 'yaj-rpc-zmq', version: yajrpc_version
    implementation group: 'info.laht', name: 'yaj-rpc-http', version: yajrpc_version
}
```

To get started head over to the [Wiki](https://github.com/markaren/YAJ-RPC/wiki)!