package core.comparator.util

/**
 * @author DrkCore
 * @since 11/5/18
 */
fun <T> forPair(list: List<T>): List<Pair<T, T>> {
    val results = mutableListOf<Pair<T, T>>()
    for (i in 0 until list.size) {
        val left = list[i]
        for (j in i + 1 until list.size) {
            results.add(Pair(left, list[j]))
        }
    }
    return results
}