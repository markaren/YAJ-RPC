/*
 * The MIT License
 *
 * Copyright 2018 Lars Ivar Hatledal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package info.laht.yaj_rpc.net

import info.laht.yaj_rpc.RpcParams
import info.laht.yaj_rpc.RpcRequestOut
import info.laht.yaj_rpc.RpcResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

private const val DEFAULT_TIME_OUT: Long = 1000
typealias Consumer<T> = (T) -> Unit

interface RpcClient: Closeable {


    fun notify(methodName: String) {
        notify(methodName, RpcParams.noParams())
    }

    fun notify(methodName: String, params: RpcParams)

    @Throws(TimeoutException::class)
    fun write(methodName: String): RpcResponse {
        return write(methodName, RpcParams.noParams(), DEFAULT_TIME_OUT)
    }

    @Throws(TimeoutException::class)
    fun write(methodName: String, params: RpcParams): RpcResponse {
        return write(methodName, params, DEFAULT_TIME_OUT)
    }

    @Throws(TimeoutException::class)
    fun write(methodName: String, params: RpcParams, timeOut: Long = DEFAULT_TIME_OUT): RpcResponse

    fun writeAsync(methodName: String, params: RpcParams, callback: Consumer<RpcResponse>)
    fun writeAsync(methodName: String, callback: Consumer<RpcResponse>) {
        writeAsync(methodName, RpcParams.noParams(), callback)
    }

}

abstract class AbstractRpcClient : RpcClient  {

    private val callbacks = mutableMapOf<String, Consumer<RpcResponse>>()

    override fun notify(methodName: String, params: RpcParams) {
        internalWrite(RpcRequestOut(methodName, params).toJson())
    }

    override fun writeAsync(methodName: String, params: RpcParams, callback: Consumer<RpcResponse>) {
        val request = RpcRequestOut(methodName, params).apply {
            id = UUID.randomUUID().toString()
            callbacks[id.toString()] = callback
        }.toJson()
        internalWrite(request)
    }


    @Throws(TimeoutException::class)
    override fun write(methodName: String, params: RpcParams, timeOut: Long): RpcResponse {

        var response: RpcResponse? = null
        val latch = CountDownLatch(1)
        val request = RpcRequestOut(methodName, params).apply {
            id = UUID.randomUUID().toString()
            callbacks[id.toString()] = {
                response = it
                latch.countDown()
            }
        }.toJson()
        internalWrite(request)
        if (!latch.await(timeOut, TimeUnit.MILLISECONDS)) {
            throw TimeoutException("Timeout")
        }
        return response!!

    }

    protected abstract fun internalWrite(msg: String)

    protected fun messageReceived(message: String) {
        val response = RpcResponse.fromJson(message)
        if (response.error != null) {
            LOG.warn("RPC invocation returned error: ${response.error}")
        } else {
            val id = response.id.toString()
            callbacks[id]?.also { callback ->
                callback.invoke(response)
            }
            callbacks.remove(id)
        }
    }

    private companion object {
        val LOG: Logger = LoggerFactory.getLogger(AbstractRpcClient::class.java)
    }

}