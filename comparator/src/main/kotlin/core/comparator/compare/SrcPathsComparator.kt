package core.comparator.compare

import core.comparator.compare.IDepthLimit.Companion.DEFAULT_DEPTH
import core.comparator.saver.ISaver
import core.comparator.saver.SrcPathsHitSaver
import core.comparator.util.Node
import core.comparator.util.Tree
import java.io.File

/**
 * @author DrkCore
 * @since 10/30/18
 */
class SrcPathHit(override val left: Apk, override val right: Apk, val set: Set<String>) : Weightable {

    override fun getWeight(): Float = set.size.toFloat()

}

class SrcPathHitComparator(threshold: Int = 1) : IComparator<SrcPathHit>, IDepthLimit {

    override var depth: Int = DEFAULT_DEPTH

    override val saver: ISaver<SrcPathHit> = SrcPathsHitSaver(threshold.toFloat())

    override fun compare(left: Apk, right: Apk): SrcPathHit {
        val tree = Tree<Boolean>()
        for (srcPath in left.srcPaths) {
            var node = tree.root
            for (s in srcPath.split(File.separator)) {
                if (s.isNotEmpty()) {
                    node = node.getOrCreateChild(s)
                }
            }
        }

        for (srcPath in right.srcPaths) {
            var node: Node<Boolean>? = tree.root
            val array = srcPath.split(File.separator)
            for (i in 0 until array.size) {
                val s = array[i]
                if (s.isNotEmpty()) {
                    val nextNode: Node<Boolean>? = node!!.getChild(s)
                    if (nextNode == null || i == array.size - 1) {
                        var cursor: Node<Boolean>? = nextNode ?: node
                        // reach the last node in common
                        while (cursor != null) {
                            cursor.content = true
                            cursor = cursor.parent
                        }
                        break

                    } else {
                        node = nextNode
                    }
                }
            }
        }

        val commonNodes = tree.dump(object : Tree.Filter<Boolean> {
            override fun accept(node: Node<Boolean>): Boolean {
                if (node.content == true) {
                    if (node.isEmpty()) {
                        return true
                    } else {
                        for (value in node.child.values) {
                            if (value.content == true) {
                                return false
                            }
                        }
                        return true
                    }
                }
                return false
            }
        })

        val commonSet = mutableSetOf<String>()
        nodeLoop@ for (node in commonNodes) {
            val path = node.getFullPath(".")

            if (path.contains(".")) {
                if (path.startsWithIgnoredPkg()) {
                    continue@nodeLoop
                }

                if (path.startsWith("com") || path.startsWith("org") && path.length >= 5) {
                    var allMatch = true
                    for (i in 5 until path.length) {
                        if (i % 2 == 1 && (path[i] != '.' || !path[i - 1].isLetter())) {
                            allMatch = false
                            break
                        }
                    }
                    if (allMatch) {
                        continue@nodeLoop
                    }
                }

                commonSet.add(path)
            }
        }

        return SrcPathHit(left, right, commonSet)
    }

}