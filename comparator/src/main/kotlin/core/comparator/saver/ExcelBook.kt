package core.comparator.saver

import jxl.Workbook
import jxl.format.Colour
import jxl.write.Label
import jxl.write.WritableCellFormat
import jxl.write.WritableSheet
import java.io.File

/**
 * @author DrkCore
 * @since 10/30/18
 */
class ExcelBook(private val xls: File) {

    private val book = Workbook.createWorkbook(xls)

    fun close() {
        book.write()
        book.close()
    }

    class Sheet(val name: String, private val sheet: WritableSheet, private val idx: Int) {

        companion object {
            val LIGHT_COLORS = arrayOf(Colour.YELLOW, Colour.GOLD, Colour.LIGHT_ORANGE, Colour.BRIGHT_GREEN)
        }

        private var colorIdx = -1

        fun nextLightColor(): Colour {
            colorIdx++
            if (colorIdx < 0 || colorIdx >= LIGHT_COLORS.size) {
                colorIdx = 0
            }
            return LIGHT_COLORS[colorIdx]
        }

        fun add(col: Int, row: Int, content: String, color: Colour? = null) {
            val cellFormat = WritableCellFormat()
            if (color != null) {
                cellFormat.setBackground(color)
            }

            val productLabel = Label(col, row, content)
            productLabel.cellFormat = cellFormat
            sheet.addCell(productLabel)
        }

    }

    private val sheets = mutableListOf<Sheet>()

    fun newSheet(name: String): Sheet {
        val idx = sheets.size
        val sheet = Sheet(name, book.createSheet(name, idx), idx)
        sheets.add(sheet)
        return sheet
    }

}