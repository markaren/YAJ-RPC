package info.laht.yaj_rpc.net.http

import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.net.AbstractRpcClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private const val GET = "GET"
private const val POST = "POST"

class RpcHttpClient(
         host: String,
         port: Int
): AbstractRpcClient() {

    private val baseUrl = "http://$host:$port"

    override fun write(msg: String, isNotification: Boolean) {

        val url = "$baseUrl/jsonrpc?${URLEncoder.encode(msg.replace("\\s".toRegex(), ""), "UTF-8")}"
        val con = URL(url).openConnection() as HttpURLConnection
        con.setRequestProperty("Content-Type", "application/json-rpc")

        if (isNotification) {
            con.requestMethod = POST
        } else {
            con.requestMethod = GET

            val response = StringBuilder().apply {
                BufferedReader(InputStreamReader(con.inputStream)).use {
                    while (true) {
                        it.readLine()?.also { append(it) } ?: break
                    }
                }
            }.toString()

            messageReceived(response)

        }

    }

    override fun close() {

    }
}

