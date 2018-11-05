package core.comparator.compare

import core.comparator.saver.ISaver
import core.comparator.saver.SrcSumsHitSaver

/**
 * @author DrkCore
 * @since 10/31/18
 */
class SrcSumsHit(override val left: Apk, override val right: Apk, val hit: Map<String, Pair<Set<String>, Set<String>>>) : Weightable {

    override fun getWeight(): Float {
        return hit.size.toFloat()
    }

}

class SrcSumsComparator(threshold: Int = 1) : IComparator<SrcSumsHit>, IDepthLimit {

    override var depth: Int = IDepthLimit.DEFAULT_DEPTH

    override val saver: ISaver<SrcSumsHit> = SrcSumsHitSaver(threshold.toFloat())

    override fun compare(left: Apk, right: Apk): SrcSumsHit {
        val hits = mutableMapOf<String, Pair<Set<String>, Set<String>>>()
        for (key in left.smaliDigest.keys) {
            val hit = right.smaliDigest[key]
            if (hit != null) {
                val leftHit = left.smaliDigest[key]!!
                val rightHit = right.smaliDigest[key]!!

                if (leftHit.size == 1 && leftHit.first().startsWithIgnoredPkg() && rightHit.size == 1 && rightHit.first().startsWithIgnoredPkg()) {
                    continue
                }

                hits[key] = Pair(
                        leftHit,
                        rightHit
                )
            }
        }
        return SrcSumsHit(left, right, hits)
    }

}