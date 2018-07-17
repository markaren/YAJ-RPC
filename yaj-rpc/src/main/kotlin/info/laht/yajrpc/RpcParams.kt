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

package info.laht.yajrpc

import com.google.gson.*
import java.lang.reflect.Type

/**
 * @author Lars Ivar Hatledal
 */
sealed class RpcParams {

    abstract val paramCount: Int

    companion object {

        @JvmStatic
        fun noParams() = RpcNoParams

        @JvmStatic
        fun listParams(value: List<Any>) = RpcListParams(value)

        @JvmStatic
        fun listParams(vararg value: Any) = RpcListParams(value.toList())

        @JvmStatic
        fun mapParams(value: Map<String, Any>) = RpcMapParams(value)

        @JvmStatic
        fun mapParams(vararg value: Pair<String, Any>) = RpcMapParams(value.toMap())

    }

}

/**
 * @author Lars Ivar Hatledal
 */
object RpcNoParams : RpcParams() {

    override val paramCount: Int
        get() = 0

}

/**
 * @author Lars Ivar Hatledal
 */
class RpcListParams<out E>(
        val value: List<E>
) : RpcParams() {

    constructor(vararg value: E) : this(value.toList())

    override val paramCount: Int
        get() = value.size

    override fun toString(): String {
        return "RpcListParams(value=$value)"
    }

}

/**
 * @author Lars Ivar Hatledal
 */
class RpcMapParams<out E>(
        val value: Map<String, E>
) : RpcParams() {

    override val paramCount: Int
        get() = value.size

    override fun toString(): String {
        return "RpcMapParams(value=$value)"
    }

}

/**
 * @author Lars Ivar Hatledal
 */
class RpcParamsTypeAdapter : JsonDeserializer<RpcParams>, JsonSerializer<RpcParams> {

    override fun serialize(src: RpcParams, typeOfSrc: Type, context: JsonSerializationContext): JsonElement? {

        return when (src) {
            RpcNoParams -> null
            is RpcListParams<*> -> YAJRPC.toJsonTree(src.value)
            is RpcMapParams<*> -> YAJRPC.toJsonTree(src.value)
        }

    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): RpcParams {
        return when {
            json.isJsonArray -> RpcListParams<JsonElement>(json.asJsonArray.toMutableList())
            json.isJsonObject -> RpcMapParams<JsonElement>(json.asJsonObject.entrySet().associate { it.key to it.value })
            else -> RpcNoParams
        }
    }
}
