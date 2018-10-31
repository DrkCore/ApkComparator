package core.comparator.compare

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

class SrcPathHitComparator : IComparator<SrcPathHit> {

    override val saver: ISaver<SrcPathHit> = SrcPathsHitSaver()

    companion object {
        private val IGNORE_PKGS = arrayOf<String>(
                "com.appsflyer",
                "com.google",
                "com.facebook",
                "io.fabric",
                "android.",
                "com.android",
                "com.applovin",
                "com.squareup",
                "com.flurry"
        )
    }

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
            for (s in srcPath.split(File.separator)) {
                if (s.isNotEmpty()) {
                    val nextNode: Node<Boolean>? = node!!.getChild(s)
                    if (nextNode != null) {
                        node = nextNode

                    } else {
                        // reach the last node in common
                        while (node != null) {
                            node.content = true
                            node = node.parent
                        }
                        break
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
                for (ignore in IGNORE_PKGS) {
                    if (path.startsWith(ignore)) {
                        continue@nodeLoop
                    }
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