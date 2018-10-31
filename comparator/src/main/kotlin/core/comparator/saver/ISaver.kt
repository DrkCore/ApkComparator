package core.comparator.saver

import core.comparator.compare.Weightable

/**
 * @author DrkCore
 * @since 10/30/18
 */
interface ISaver<T : Weightable> {

    val threshold: Float

    fun save(book: ExcelBook, results: List<T>)
}