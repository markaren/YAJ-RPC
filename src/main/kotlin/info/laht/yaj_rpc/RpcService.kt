package info.laht.yaj_rpc

import java.lang.reflect.Method

interface RpcService {

    val name: String

    companion object {

        private val map = hashMapOf<RpcService, Set<Method>>()

        internal fun getExposedMethods(service: RpcService): Set<Method> {
            if (service !in map) {
                map[service] = mutableSetOf<Method>().apply {
                    for (method in service.javaClass.declaredMethods) {
                        if (method.isAnnotationPresent(RpcMethod::class.java)) {
                            add(method)
                        }
                    }
                }
            }

            return map[service]!!
        }

        internal fun getExposedMethod(service: RpcService, name: String, numParams: Int): Method? {
            return getExposedMethods(service).firstOrNull { m: Method ->
                m.name == name && m.parameterCount == numParams
            }
        }

        internal fun getCallDescription(service: RpcService): Map<String, Any> {

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

            return getExposedMethods(service).associate { it.name to toMap(it)}

        }

    }

}