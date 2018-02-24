///*
// * The MIT License
// *
// * Copyright 2018 Lars Ivar Hatledal
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//
//package info.laht.yaj_rpc
//
//import java.util.Arrays
//
///**
// * @author Lars Ivar Hatledal laht@ntnu.no.
// */
//class RpcParams internal constructor(
//        val params: Any?
//) {
//
//    val paramsType: Class<*>
//        get() = params!!.javaClass
//
//    val paramCount: Int
//        get() {
//            return when {
//                isList || isArray -> asArray()!!.size
//                isMap -> asMap()!!.size
//                else -> 0
//            }
//        }
//
//    val isNull: Boolean
//        get() = params == null
//
//    val isList: Boolean
//        get() = !isNull && params is List<*>
//
//    val isArray: Boolean
//        get() = !isNull && params!!.javaClass.isArray
//
//    val isMap: Boolean
//        get() = !isNull && params is Map<*, *>
//
//    fun asMap(): Map<String, Any> {
//        return if (isMap) params as Map<String, Any> else throw AssertionError()
//    }
//
////    fun asList(): List<Any> {
////        if (isList) {
////            return params as List<Any>
////        } else if (isArray) {
////            return Arrays.asList(*asArray())
////        }
////        return throw AssertionError()
////    }
//
//    fun asArray(): Array<Any?> {
//        if (isList) {
//            return (params as List<Any?>).toTypedArray()
//        } else if (isArray) {
//            return params as Array<Any?>
//        }
//        return throw AssertionError()
//    }
//
//    override fun toString(): String {
//        return "JsonRPCParams{" + params + '}'.toString()
//    }
//}
