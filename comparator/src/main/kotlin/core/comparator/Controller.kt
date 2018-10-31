package core.comparator

import core.comparator.compare.Apk
import core.comparator.compare.IComparator
import core.comparator.compare.Weightable
import core.comparator.saver.ExcelBook
import java.io.File

/**
 * @author DrkCore
 * @since 10/30/18
 */
class Controller {

    companion object {
        const val XLS = "compared_result.xls"
    }

    val comparators = mutableListOf<IComparator<out Weightable>>()

    fun process(files: Array<File>, outDir: File) {
        val apks = mutableListOf<Apk>()
        files.forEach { apks.add(Apk.from(it, outDir)) }

        val book = ExcelBook(File(outDir, XLS))

        for (comparator in comparators) {
            val results: List<Weightable> = comparator.compareAll(apks = apks)
            if (results.isNotEmpty()) {
                @Suppress("UNCHECKED_CAST")
                comparator.saver.save(book, results as List<Nothing>)
                break
            }
        }

        book.close()

    }

}