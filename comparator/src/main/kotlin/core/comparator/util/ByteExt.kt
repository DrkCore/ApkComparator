package core.comparator.util

import java.nio.charset.Charset


/**
 * @author DrkCore
 * @since 10/30/18
 */
fun ByteArray.toHex(upperCase: Boolean = false, charset: Charset = Charset.defaultCharset()): String {
    var hex = ""
    if (this.isNotEmpty()) {
        val len = this.size
        val builder = StringBuilder(len * 2)

        var tmp: String
        for (b in this) {
            tmp = if (upperCase) Integer.toHexString(0xFF and b.toInt()).toUpperCase() else Integer.toHexString(0xFF and b.toInt())
            if (tmp.length == 1) {
                builder.append("0").append(tmp)
            } else
                builder.append(tmp)
        }
        hex = builder.toString()
    }
    return hex
}