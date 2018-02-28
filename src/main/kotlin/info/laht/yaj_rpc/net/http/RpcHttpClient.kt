package info.laht.yaj_rpc.net.http

import info.laht.yaj_rpc.net.AbstractRpcClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class RpcHttpClient(
         host: String,
         port: Int
): AbstractRpcClient() {

    private val baseUrl = "http://$host:$port/jsonrpc?"

    override fun write(msg: String): String {

        val urlFriendlyString = URLEncoder.encode(msg.replace("\\s".toRegex(), ""), "UTF-8")
        val url = "$baseUrl$urlFriendlyString"

        val con = URL(url).openConnection() as HttpURLConnection
        con.setRequestProperty("Content-Type", "application/json-rpc")
        con.requestMethod = "POST"

        return StringBuilder().apply {
            BufferedReader(InputStreamReader(con.inputStream)).use {
                while (true) {
                    it.readLine()?.also { append(it) } ?: break
                }
            }
        }.toString()

    }

    /**
     * This method has no effect for
     * instances of RpcHttpClient
     */
    override fun close() {
        //nothing to do
    }
}

