package core.comparator.compare

import brut.androlib.ApkDecoder
import core.comparator.util.dumpDir
import core.comparator.util.dumpFile
import core.comparator.util.md5
import java.io.File
import java.io.FileFilter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * @author DrkCore
 * @since 2017-06-18
 */
class Apk private constructor(val apk: File, val extraDir: File) {

    val pkgName: String by lazy {
        val doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(File(extraDir, "AndroidManifest.xml"))
        val nodeList = doc.getElementsByTagName("manifest")
        val node = nodeList.item(0)
        node.attributes.getNamedItem("package").nodeValue
    }
    val strings: Map<String, Set<String>> by lazy {
        val strings = mutableMapOf<String, Set<String>>()
        val valDirs = File(extraDir, "res").listFiles { pathname -> pathname.isDirectory && pathname.name.startsWith(VALUES_PREFIX) }
        valDirs?.forEach {
            val xml = File(it, XML_NAME)
            if (xml.isFile) {
                val lines = readStrings(xml)
                if (this.apk != IGNORED_APK) {
                    val ignoreLines = ignoredApk!!.strings[it.name]
                    if (ignoreLines != null) {
                        lines.removeAll(ignoreLines)
                    }
                }
                strings[it.name] = lines
            }
        }
        strings
    }
    val srcPaths: Set<String> by lazy {
        val srcPaths = mutableSetOf<String>()
        for (file in extraDir.listFiles()) {
            if (file.isDirectory && file.name.startsWith("smali")) {
                for (dir in file.dumpDir()) {
                    val ignoreLen = file.absolutePath.length
                    srcPaths.add(dir.absolutePath.substring(ignoreLen, dir.absolutePath.length))
                }
            }
        }
        srcPaths
    }
    var selfConflictSum = mutableSetOf<String>()
    val smaliDigest: Map<String, Set<String>> by lazy {
        val smaliSums = mutableMapOf<String, Set<String>>()

        for (item in extraDir.listFiles()) {
            if (item.isDirectory && item.name.startsWith("smali")) {
                item.dumpFile(FileFilter { file -> file.isFile && file.extension.toLowerCase() == "smali" }).forEach {
                    val md5 = it.md5()
                    val paths = smaliSums[md5]

                    val path = it.absolutePath.substring(item.absolutePath.length + 1, it.absolutePath.length).replace(File.separator, ".")
                    if (paths == null) {
                        smaliSums[md5] = mutableSetOf(path)
                    } else {
                        val mutableSet: MutableSet<String> = paths as MutableSet<String>
                        mutableSet.add(path)

                        selfConflictSum.add(md5)
                    }
                }
            }
        }

        smaliSums
    }

    private fun readStrings(xml: File): MutableSet<String> {
        val strings = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xml)
                .getElementsByTagName("string")

        val len = strings.length
        val set = HashSet<String>(len)
        for (i in 0 until len) {
            val line = strings.item(i)
            set.add(line.textContent)
        }
        return set
    }

    override fun toString(): String {
        return "Apk(apk=$apk, extraDir=$extraDir, pkgName='$pkgName')"
    }

    companion object {

        const val EXT = "apk"
        const val VALUES_PREFIX = "values"
        const val DEFAULT_VALUES = "values"
        const val XML_NAME = "strings.xml"

        fun fromDir(apkDir: File, outDir: File): List<Apk> {
            val apkFiles = apkDir.listFiles { _, name -> name.toLowerCase().endsWith(EXT) }
            val len = apkFiles?.size ?: 0
            val apks = mutableListOf<Apk>()

            for (i in 0 until len) {
                apks[i] = from(apkFiles!![i], outDir)
            }
            return apks
        }

        private val IGNORED_APK = File("libs/core.ignore.apk")
        private var ignoredApk: Apk? = null

        fun from(apk: File, outDir: File): Apk {
            if (ignoredApk == null) {
                ignoredApk = decode(IGNORED_APK, outDir)
            }
            return decode(apk, outDir)
        }

        private fun decode(apk: File, outDir: File): Apk {
            val md5 = apk.md5()

            val extractDir = File(outDir, "${apk.name}_${md5}")

            if (!extractDir.isDirectory) {
                val tmpDir = File(outDir, "${apk.name}_${md5}_tmp")
                tmpDir.deleteRecursively()
                val decoder = ApkDecoder()
                decoder.setApkFile(apk)
                decoder.setOutDir(tmpDir)
                decoder.decode()
                tmpDir.renameTo(extractDir)
            }
            return Apk(apk, extractDir)
        }

    }

    /*Group*/

    private var group: MutableSet<Apk>? = null

    fun getGroup(): Set<Apk>? {
        return group
    }

    fun merge(target: Apk) {
        val merge: MutableSet<Apk>?
        if (group == null && target.group == null) {
            merge = HashSet()

        } else if (group != null && target.group != null) {
            merge = group
            merge!!.addAll(target.group!!)

        } else if (group != null) {
            merge = group

        } else {//target.group != null
            merge = target.group

        }
        merge!!.add(this)
        merge.add(target)

        merge.forEach { apk -> apk.group = merge }
    }

}
