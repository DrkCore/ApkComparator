package core.comparator.util

import java.io.File
import java.io.FileFilter
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

fun File.dumpDir(allowHiddenDir: Boolean = false): List<File> {
    return dumpDir(this, filter = FileFilter { it.isDirectory && !it.isHidden && it != this })
}

fun File.dumpFile(filter: FileFilter? = null, allowHiddenDir: Boolean = false): List<File> {
    return dumpFile(this, filter = filter, allowHiddenDir = false)
}

fun dumpFile(item: File, list: MutableList<File>? = null, filter: FileFilter? = null, allowHiddenDir: Boolean = false): List<File> {
    var result = list
    if (result == null) {
        result = mutableListOf()
    }

    if (item.isFile) {
        if (filter == null || filter.accept(item)) {
            result.add(item)
        }
    } else if (item.isDirectory && (!item.isHidden || allowHiddenDir)) {// 是目录，不是隐藏目录，或者允许隐藏目录
        val files = item.listFiles()
        for (i in 0 until (files?.size ?: 0)) {
            dumpFile(files!![i], result, filter, allowHiddenDir)
        }
    }

    return result
}

fun dumpDir(item: File, list: MutableList<File>? = null, filter: FileFilter? = null): List<File> {
    var result = list
    if (result == null) {
        result = mutableListOf()
    }

    if (item.isDirectory) {
        if (filter == null || filter.accept(item)) {
            result.add(item)
        }

        val files = item.listFiles()
        for (i in 0 until (files?.size ?: 0)) {
            dumpDir(files!![i], result, filter)
        }
    }

    return result
}
