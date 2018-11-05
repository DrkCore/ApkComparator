package core.comparator.compare

import core.comparator.saver.ISaver
import core.comparator.util.forPair

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
        forPair(apks).forEach {
            results.add(compare(it.first, it.second))
        }

        results.sortByDescending { t -> t.getWeight() }

        return results
    }

    fun compare(left: Apk, right: Apk): T

    val saver: ISaver<T>
}
