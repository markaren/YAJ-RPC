package info.laht.yaj_rpc

import com.google.gson.*
import java.lang.reflect.Type

sealed class RpcParams {

    abstract val paramCount: Int

}

object RpcNoParams: RpcParams() {
    override val paramCount: Int
        get() = 0
}

class RpcListParams(
        val value: MutableList<JsonElement>
): RpcParams() {

    override val paramCount: Int
        get() = value.size

    override fun toString(): String {
        return "RpcListParams(value=$value)"
    }

}

class RpcMapParams(
        val value: Map<String, JsonElement>
): RpcParams() {

    override val paramCount: Int
        get() = value.size

    override fun toString(): String {
        return "RpcListParams(value=$value)"
    }

}

class RpcParamsDeserializer: JsonDeserializer<RpcParams> {
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): RpcParams {
        return when {
            json.isJsonArray -> RpcListParams(json.asJsonArray.toMutableList())
            json.isJsonObject -> RpcMapParams(json.asJsonObject.entrySet().associate { it.key to it.value })
            else ->  RpcNoParams
        }

    }
}

class Test {

    val params: RpcParams? = null

}

fun main (args: Array<String>) {

    val json = """
        {
            "params": {"per": "nils"}
        }
        """

    val gson = GsonBuilder().apply {
        registerTypeAdapter(RpcParams::class.java, RpcParamsDeserializer())
    }.create()



    println(gson.fromJson(json, Test::class.java).params)

}