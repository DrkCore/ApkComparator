package core.comparator.compare

import java.util.*

/**
 * @author DrkCore
 * @since 10/30/18
 */
class StringsHit(val left: Apk, val right: Apk, private val valDir: String = Apk.DEFAULT_VALUES) : Weightable {

    val hit: Set<String>
    val leftCount: Int
    val rightCount: Int
    val avgPct: Float

    init {
        val left = this.left.strings[valDir]!!
        val right = this.right.strings[valDir]!!

        val set = HashSet<String>()
        set.addAll(left)
        set.retainAll(right)
        set.remove("")
        hit = set
        leftCount = left.size
        rightCount = right.size

        avgPct = if (leftCount > 0 && rightCount > 0) {
            ((hit.size / leftCount.toFloat()) + (hit.size / rightCount.toFloat())) / 2F
        } else {
            0F
        }
    }

    override fun getWeight(): Float = avgPct

    override fun toString(): String {
        return "${left.pkgName}(${leftCount})  x  ${right.pkgName}(${rightCount}) = hit.size"
    }

}

class StringsHitComparator : IComparator<StringsHit> {
    override fun compare(left: Apk, right: Apk): StringsHit {
        return StringsHit(left, right)
    }
}