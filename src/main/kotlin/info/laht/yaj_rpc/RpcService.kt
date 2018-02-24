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

open class RpcService(
        val name: String
) {

    private val exposedMethods = mutableSetOf<Method>()

    init {
        for (method in javaClass.declaredMethods) {
            if (method.isAnnotationPresent(RpcMethod::class.java)) {
                exposedMethods.add(method)
            }
        }
        LOG.info("RpcService created, named: $name")
    }

    internal fun getCallDescription(): Map<String, Any> {
        return mutableMapOf<String, Any>().also {
            for (m in exposedMethods) {
                val map1 = HashMap<String, Any>()
                val list = mutableListOf<String>()
                for (p in m.parameters) {
                    list.add(p.name + ":" + p.type.simpleName)
                }
                val returnType = m.returnType
                map1[PARAMS_KEY] = if (list.isEmpty()) "void" else list
                map1[RESULT_KEY] = if (returnType == null) "void" else returnType.simpleName

                it[m.name] = map1
            }
        }
    }

    internal fun getExposedMethod(name: String, numParams: Int): Method? {
        return exposedMethods.firstOrNull { m: Method ->
            m.name == name && m.parameterCount == numParams
        }
    }

    private companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcService::class.java)
    }

}