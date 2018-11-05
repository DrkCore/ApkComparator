package core.comparator

import core.comparator.compare.*
import core.comparator.saver.ExcelBook
import core.comparator.util.forPair
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import java.io.File
import java.io.FileFilter

/**
 * @author DrkCore
 * @since 10/30/18
 */
class Controller(comparators: List<IComparator<out Weightable>>? = null) {

    private val comparators: List<IComparator<out Weightable>> = comparators ?: listOf(
            StringsHitComparator(),
            SrcPathHitComparator(),
            SrcSumsComparator()
    )

    companion object {
        const val XLS_NAME = "compared_result"
        const val XLS_EXT = "xls"
    }

    fun process(files: Array<File>, outDir: File, merge: Boolean = false) {
        val apks = mutableListOf<Apk>()
        files.forEach { apks.add(Apk.from(it, outDir)) }

        outDir.listFiles(FileFilter { it.extension.toLowerCase() == XLS_EXT })?.forEach { it.delete() }

        if (merge) {
            val book = ExcelBook(File(outDir, "${XLS_NAME}.${XLS_EXT}"))
            for (comparator in comparators) {
                val results: List<Weightable> = comparator.compareAll(apks = apks)
                @Suppress("UNCHECKED_CAST")
                comparator.saver.save(book, results as List<Nothing>)
            }
            book.close()
        } else {
            for (pair in forPair(apks)) {
                val book = ExcelBook(File(outDir, "${pair.first.pkgName} x ${pair.second.pkgName}.${XLS_EXT}"))
                for (comparator in comparators) {
                    val results: List<Weightable> = comparator.compareAll(listOf(pair.first, pair.second))
                    @Suppress("UNCHECKED_CAST")
                    comparator.saver.save(book, results as List<Nothing>)
                }
                book.close()
            }
        }
    }

    fun main(args: Array<String>) {
        val options = Options()
        options.addOption("h", "help", false, "print usage")
        options.addOption("d", "dir", true, "specify a dir to compare all apks in it")
        options.addOption("m", "merge", false, "if set, all compared results will be merged into one xml file")
//        options.addOption("l", "limit-depth", true, "limit depth of  package in some comparators, e.g. SrcPathComparator")
        options.addOption("o", "output", true, "specify directory to store the output files")

        val cmd = DefaultParser().parse(options, args)
        if (cmd.hasOption("h")) {
            HelpFormatter().printHelp("comparator", options)
            return
        }

        //========================================
        lateinit var apks: Array<File>
        if (!cmd.hasOption("d")) {
            println("dir option must be specified")
            System.exit(1)
            return

        } else {
            val path = cmd.getOptionValue("d")
            if (path.isNullOrBlank()) {
                println("dir option must not be empty")
                System.exit(1)
                return
            }
            val dir = File(path).absoluteFile
            if (!dir.isDirectory) {
                println("${dir} not exists, or is not a directory")
                System.exit(1)
                return
            }

            val tmp = dir.listFiles(FileFilter { it.isFile && it.extension.toLowerCase() == "apk" })
            if (tmp == null || tmp.isEmpty()) {
                println("no apks detected in the dir ${dir}")
                System.exit(1)
                return

            } else if (tmp.size <= 1) {
                println("no enough apks to be compared")
                System.exit(1)
                return
            }
            apks = tmp
        }

        var outputDir = File("./out").absoluteFile
        if (cmd.hasOption("o")) {
            val path = cmd.getOptionValue("o")
            if (path.isNullOrBlank()) {
                println("output option is not valid:\"${path}\"")
                System.exit(1)
                return
            }
            val dir = File(path).absoluteFile
            if (!dir.isDirectory && !dir.mkdirs()) {
                println("${dir} is not a directory and could not be created as a directory")
                System.exit(1)
                return
            }
            outputDir = dir
        }

        val merge: Boolean = cmd.hasOption("m")

        if (cmd.hasOption("l")) {
            val value = cmd.getOptionValue("l")
            val limit = value?.toIntOrNull()
            if (limit == null) {
                println("could not convert limit depth value \"${value}\" to Int")
                System.exit(1)
                return

            } else if (limit < 2) {
                println("limit depth must bigger than 2, current value is \"${value}\"")
                System.exit(1)
                return
            }
            for (comparator in comparators) {
                if (comparator is IDepthLimit) {
                    comparator.depth = limit
                }
            }
        }

        process(apks, outputDir, merge)
        println()
        println("======================================================")
        println("Processing finished, you can find result file under ${outputDir}")
    }

}