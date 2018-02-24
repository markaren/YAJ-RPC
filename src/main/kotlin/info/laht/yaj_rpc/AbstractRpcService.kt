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

package info.laht.yaj_rpc

import java.lang.reflect.Method
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractRpcService(
        val name: String
) {

    private val exposedMethods = mutableSetOf<Method>()

    init {
        for (method in javaClass.declaredMethods) {
            if (method.isAnnotationPresent(RpcMethod::class.java)) {
                exposedMethods.add(method)
            }
        }
        LOG.info("RpcService '$name' created")
    }

    internal fun getCallDescription(): Map<String, Any> {

        fun toMap(m: Method): Map<String, Any> {
            return mutableMapOf<String, Any>().also { map1 ->
                val list = List(m.parameterCount, { i ->
                    m.parameters[i].let {
                        "${it.name}:${it.type.simpleName}"
                    }
                })
                map1[PARAMS_KEY] = if (list.isEmpty()) "void" else list
                map1[RESULT_KEY] = if (m.returnType == null) "void" else m.returnType.simpleName
            }
        }

        return exposedMethods.associate { it.name to toMap(it)}

    }

    internal fun getExposedMethod(name: String, numParams: Int): Method? {
        return exposedMethods.firstOrNull { m: Method ->
            m.name == name && m.parameterCount == numParams
        }
    }

    private companion object {
        val LOG: Logger = LoggerFactory.getLogger(AbstractRpcService::class.java)
    }

}