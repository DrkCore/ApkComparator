package core.comparator.ext

import java.io.File
import java.security.MessageDigest


/**
 * @author DrkCore
 * @since 10/30/18
 */
fun File.md5(): String {
    val digest = MessageDigest.getInstance("MD5")
    digest.reset()
    return digest.digest(this.readBytes()).toHex()
}