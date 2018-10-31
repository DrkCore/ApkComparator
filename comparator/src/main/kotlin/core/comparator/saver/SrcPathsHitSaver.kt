package core.comparator.saver

import core.comparator.compare.SrcPathHit

/**
 * @author DrkCore
 * @since 10/30/18
 */
class SrcPathsHitSaver(override val threshold: Float = 1F) : ISaver<SrcPathHit> {
    override fun save(book: ExcelBook, results: List<SrcPathHit>) {
        val sheet = book.newSheet("SrcPathsHit")
        for (i in 0 until results.size) {
            val topColor = sheet.nextLightColor()
            val result = results[i]
            var row = 0;
            sheet.add(i, row++, result.left.pkgName, topColor)
            sheet.add(i, row++, result.right.pkgName, topColor)

            for (s in result.set) {
                sheet.add(i, row++, s)
            }
        }
    }

}