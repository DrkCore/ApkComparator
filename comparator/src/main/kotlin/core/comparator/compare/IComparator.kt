package core.comparator.compare

import core.comparator.saver.ISaver

/**
 * @author core
 * @since 17-6-21
 */
interface Weightable {
    val left: Apk
    val right: Apk
    fun getWeight(): Float
}

interface IComparator<T : Weightable> {

    fun compareAll(apks: List<Apk>): List<T> {
        val len = apks.size
        if (len <= 1) {
            throw IllegalArgumentException()
        }

        val results = mutableListOf<T>()
        for (i in 0 until len) {
            val left = apks[i]
            for (j in i + 1 until len) {
                results.add(compare(left, apks[j]))
            }
        }

        results.sortByDescending { t -> t.getWeight() }

        return results
    }

    fun compare(left: Apk, right: Apk): T

    val saver: ISaver<T>
}
