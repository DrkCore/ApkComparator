package core.comparator.saver

import core.comparator.compare.Apk
import core.comparator.compare.StringsHit
import jxl.format.Colour
import java.util.*

/**
 * @author DrkCore
 * @since 10/30/18
 */
class StringsHitSaver(override val threshold: Float = 0.8F) : ISaver<StringsHit> {

    override fun canHandle(result: Any): Boolean = result is StringsHit

    override fun save(book: ExcelBook, results: List<StringsHit>) {
        val len = results.size
        var thresholdIdx = len
        for (i in 0 until len) {
            if (results.get(i).getWeight() < threshold) {
                thresholdIdx = i
                break
            }
        }

        val hit = results.subList(0, thresholdIdx)
        results.subList(thresholdIdx, len)

        saveGroup(hit, book.newSheet("StringsHit-Group"))
        saveMatrix(results, book.newSheet("StringsHit-Matrix"))
    }

    companion object {
        private const val GP_URL_PREFIX = "https://play.google.com/store/apps/details?id="
    }

    private fun saveGroup(hits: List<StringsHit>, sheet: ExcelBook.Sheet) {
        val groups = merge(hits)

        var apkCount = 0
        for (group in groups) {
            val colour = sheet.nextLightColor()

            for (apk in group) {
                sheet.add(0, apkCount, apk.pkgName, color = colour)
                sheet.add(1, apkCount, GP_URL_PREFIX + apk.pkgName, color = colour)
                apkCount++
            }
        }
    }

    private fun saveMatrix(results: List<StringsHit>, sheet: ExcelBook.Sheet) {
        val apks = HashSet<String>()
        for (result in results) {
            apks.add(result.left.pkgName)
            apks.add(result.right.pkgName)
        }
        val pkgs = ArrayList(apks)

        val colColor = sheet.nextLightColor()
        val rowColor = sheet.nextLightColor()

        var i = 0
        val len = pkgs.size
        while (i < len) {
            val pkg = pkgs[i]

            sheet.add(0, i + 1, pkg, colColor)
            sheet.add(i + 1, 0, pkg, rowColor)
            i++
        }

        for (result in results) {
            val x = pkgs.indexOf(result.left.pkgName) + 1
            val y = pkgs.indexOf(result.right.pkgName) + 1

            val color: Colour? = if (result.getWeight() >= threshold) sheet.nextLightColor() else null
            sheet.add(x, y, result.getWeight().toString(), color)
            sheet.add(y, x, result.getWeight().toString(), color)
        }
    }

    private fun merge(hits: List<StringsHit>): Set<Set<Apk>> {
        val splits = HashSet<Set<Apk>>()
        for (hit in hits) {
            hit.left.merge(hit.right)
        }
        for (result in hits) {
            splits.add(result.left.getGroup()!!)
        }
        return splits
    }

}
