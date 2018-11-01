package core.comparator.compare

import java.io.File

/**
 * @author DrkCore
 * @since 10/31/18
 */
val IGNORED_PKGS by lazy {
    File("libs/ignore_pkgs.txt").readLines()
}

fun String.startsWithIgnoredPkg(): Boolean {
    for (ignore in IGNORED_PKGS) {
        if (this.startsWith(ignore)) {
            return true
        }
    }
    return false
}