package info.laht.yaj_rpc


import java.util.HashMap

/**
 * @author Lars Ivar Hatledal
 */
class RpcError @JvmOverloads protected constructor(
        private val code: Int,
        private val message: String,
        private val data: Any? = null
) {

    enum class ErrorType private constructor(val code: Int) {

        METHOD_NOT_FOUND(-32601), PARSE_ERROR(-32700), INVALID_REQUEST(-32600), INVALID_PARAMS(-32602), INTERNAL_ERROR(-32603);

        fun toMap(data: Any?): MutableMap<String, Any> {
            return HashMap<String, Any>().also {
                it[CODE_KEY] = code
                it[MESSAGE_KEY] = toString().toLowerCase().replace("_".toRegex(), " ")
                if (data != null) {
                    it[DATA_KEY] = data
                }
            }
        }
    }

    override fun toString(): String {
        return "JsonRPCError{" + "code=" + code + ", message=" + message + ", data=" + data + '}'.toString()
    }
}
