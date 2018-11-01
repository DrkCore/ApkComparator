package core.comparator.saver

import core.comparator.compare.SrcSumsHit

/**
 * @author DrkCore
 * @since 10/31/18
 */
class SrcSumsHitSaver(override val threshold: Float = 1F) : ISaver<SrcSumsHit> {
    override fun save(book: ExcelBook, results: List<SrcSumsHit>) {
        val sheet = book.newSheet("SrcSumsHit")
        for (i in 0 until results.size) {
            val colour = sheet.nextLightColor()
            val result = results[i]
            var row = 0
            sheet.add(i, row++, result.left.pkgName, colour)
            sheet.add(i, row++, result.right.pkgName, colour)
            for (value in result.hit.values) {
                var left = "["
                for (s in value.first) {
                    left += "$s、"
                }
                left = left.substring(0, left.length - 1) + "]"

                var right = "["
                for (s in value.second) {
                    right += "$s、"
                }
                right = right.substring(0, right.length - 1) + "]"

                sheet.add(i, row++, "${left} = ${right}")
            }

        }
    }
}