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

package info.laht.yajrpc.net

import info.laht.yajrpc.RpcParams
import info.laht.yajrpc.RpcRequestOut
import info.laht.yajrpc.RpcResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.*
import java.util.concurrent.*

var DEFAULT_TIME_OUT: Long = 2000
typealias Consumer<T> = (T) -> Unit

/**
 * @author Lars Ivar Hatledal
 */
interface RpcClient : Closeable {

    @JvmDefault
    fun notify(methodName: String) {
        notify(methodName, RpcParams.noParams())
    }

    fun notify(methodName: String, params: RpcParams)

    @JvmDefault
    @Throws(TimeoutException::class)
    fun write(methodName: String): Future<RpcResponse> {
        return write(methodName, RpcParams.noParams(), DEFAULT_TIME_OUT)
    }

    @JvmDefault
    @Throws(TimeoutException::class)
    fun write(methodName: String, timeOut: Long = DEFAULT_TIME_OUT): Future<RpcResponse> {
        return write(methodName, RpcParams.noParams(), timeOut)
    }

    @JvmDefault
    @Throws(TimeoutException::class)
    fun write(methodName: String, params: RpcParams): Future<RpcResponse> {
        return write(methodName, params, DEFAULT_TIME_OUT)
    }

    @Throws(TimeoutException::class)
    fun write(methodName: String, params: RpcParams, timeOut: Long = DEFAULT_TIME_OUT): Future<RpcResponse>


}

/**
 * @author Lars Ivar Hatledal
 */
abstract class AbstractRpcClient : RpcClient {

    private val callbacks = mutableMapOf<String, Consumer<RpcResponse>>()

    private val executor = Executors.newSingleThreadExecutor();

    override fun notify(methodName: String, params: RpcParams) {
        internalWrite(RpcRequestOut(methodName, params).toJson())
    }

    @Throws(TimeoutException::class)
    override fun write(methodName: String, params: RpcParams, timeOut: Long): Future<RpcResponse> {

        fun task() = {
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
                throw TimeoutException("Timeout!")
            }
            response!!
        }

        return executor.submit(task())

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

    override fun close() {
        executor.shutdown()
    }

    private companion object {
        private val LOG: Logger = LoggerFactory.getLogger(AbstractRpcClient::class.java)
    }

}