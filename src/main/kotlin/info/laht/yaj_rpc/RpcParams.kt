package info.laht.yaj_rpc

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
object RpcNoParams: RpcParams() {

    override val paramCount: Int
        get() = 0

}

/**
 * @author Lars Ivar Hatledal
 */
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

/**
 * @author Lars Ivar Hatledal
 */
class RpcMapParams<out E>(
        val value: Map<String, E>
): RpcParams() {

    override val paramCount: Int
        get() = value.size

    override fun toString(): String {
        return "RpcListParams(value=$value)"
    }

}

/**
 * @author Lars Ivar Hatledal
 */
class RpcParamsTypeAdapter : JsonDeserializer<RpcParams>, JsonSerializer<RpcParams> {

    override fun serialize(src: RpcParams, typeOfSrc: Type, context: JsonSerializationContext): JsonElement? {

        return when(src) {
            RpcNoParams -> null
            is RpcListParams<*> -> YAJ_RPC.toJsonTree(src.value)
            is RpcMapParams<*> -> YAJ_RPC.toJsonTree(src.value)
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
