package info.laht.yaj_rpc.net.http

import info.laht.yaj_rpc.net.AbstractRpcClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset


class RpcHttpClient(
         host: String,
         port: Int
): AbstractRpcClient() {

    private val url = "http://$host:$port/jsonrpc?"

    override fun write(msg: String): String {

        val con = (URL(url).openConnection() as HttpURLConnection).apply {
            setRequestProperty("Content-Type", "application/json-rpc")
            requestMethod = "POST"
            doOutput = true
            outputStream.use {
                it.write(msg.toByteArray(Charset.forName("UTF-8")))
                it.flush()
            }
            connect()
        }

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

