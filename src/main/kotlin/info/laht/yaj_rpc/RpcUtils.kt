package info.laht.yaj_rpc

import com.google.gson.JsonElement
import java.util.HashMap
import java.util.UUID
import java.lang.reflect.Method


fun indexOf(parameterName: String, method: Method): Int {
    for (i in method.parameters.indices) {
        val p = method.parameters[i]
        if (p.name == parameterName) {
            return i
        }
    }
    return -1
}

fun getValueByIndex(index: Int, map: Map<String, JsonElement>): JsonElement {
    for ((index, value) in map.values.withIndex()) {
        if (index == index) {
            return value
        }
    }
    throw IndexOutOfBoundsException()
}

//fun getParameterNames(method: Method): List<String> {
//    return method.parameters.map { it.name }
//}
//
//fun checkParams(params: Any?) {
//    if (!(params is List<*> || params is Map<*, *> || params == null || params.javaClass.isArray)) {
//        throw IllegalArgumentException("params is not a List, Array, Map or a null value!")
//    }
//}
//
//fun createMap(method: String, params: Any?, uuid: UUID?): Map<String, Any> {
//    checkParams(params)
//    val map = HashMap<String, Any>()
//    map[JSON_RPC_IDENTIFIER] = JSON_RPC_VERSION
//    map[METHOD_KEY] = method
//    if (params != null) {
//        map[PARAMS_KEY] = params
//    }
//    if (uuid != null) {
//        map[ID_KEY] = uuid.toString()
//    }
//    return map
//}