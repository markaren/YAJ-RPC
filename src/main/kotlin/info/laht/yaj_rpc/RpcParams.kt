package info.laht.yaj_rpc

import com.google.gson.*
import info.laht.yaj_rpc.parser.JsonParser
import java.lang.reflect.Type

sealed class RpcParams {

    abstract val paramCount: Int

}

object RpcNoParams: RpcParams() {
    override val paramCount: Int
        get() = 0
}

class RpcListParams<out E>(
        val value: List<E>
): RpcParams() {

    constructor(vararg value: E): this(value.toList())

    override val paramCount: Int
        get() = value.size

    override fun toString(): String {
        return "RpcListParams(value=$value)"
    }

}

class RpcMapParams<out E>(
        val value: Map<String, E>
): RpcParams() {

    override val paramCount: Int
        get() = value.size

    override fun toString(): String {
        return "RpcListParams(value=$value)"
    }

}

class RpcParamsTypeAdapter : JsonDeserializer<RpcParams>, JsonSerializer<RpcParams> {

    override fun serialize(src: RpcParams, typeOfSrc: Type, context: JsonSerializationContext): JsonElement? {

        return when(src) {
            RpcNoParams -> null
            is RpcListParams<*> -> JsonParser.gson.toJsonTree(src.value)
            is RpcMapParams<*> -> JsonParser.gson.toJsonTree(src.value)
        }

    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): RpcParams {
        return when {
            json.isJsonArray -> RpcListParams<JsonElement>(json.asJsonArray.toMutableList())
            json.isJsonObject -> RpcMapParams<JsonElement>(json.asJsonObject.entrySet().associate { it.key to it.value })
            else ->  RpcNoParams
        }
    }
}
