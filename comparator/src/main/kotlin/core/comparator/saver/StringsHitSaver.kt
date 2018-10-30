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

        saveGroup(hit, book.newSheet("StringsHit-Group"))
        saveMatrix(results, book.newSheet("StringsHit-Matrix"))
        saveHitString(hit, book.newSheet("StringsHit-Strings"))
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

    private fun saveMatrix(hits: List<StringsHit>, sheet: ExcelBook.Sheet) {
        val apks = HashSet<String>()
        for (hit in hits) {
            apks.add(hit.left.pkgName)
            apks.add(hit.right.pkgName)
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

        for (hit in hits) {
            val x = pkgs.indexOf(hit.left.pkgName) + 1
            val y = pkgs.indexOf(hit.right.pkgName) + 1

            val color: Colour? = if (hit.getWeight() >= threshold) sheet.nextLightColor() else null
            sheet.add(x, y, hit.getWeight().toString(), color)
            sheet.add(y, x, hit.getWeight().toString(), color)
        }
    }

    private fun saveHitString(hits: List<StringsHit>, sheet: ExcelBook.Sheet) {
        var col = -1
        for (hit in hits) {
            col++

            val color = sheet.nextLightColor()
            var row = 0
            sheet.add(col, row++, "${hit.left.pkgName} [${hit.hit.size}/${hit.leftCount}]", color)
            sheet.add(col, row++, "${hit.right.pkgName} [${hit.hit.size}/${hit.rightCount}]", color)

            for (str in hit.hit) {
                sheet.add(col, row++, str)
            }
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
