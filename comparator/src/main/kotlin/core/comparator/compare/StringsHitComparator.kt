package core.comparator.compare

import java.util.*

/**
 * @author DrkCore
 * @since 10/30/18
 */
class StringsHit(val left: Apk, val right: Apk, private val valDir: String = Apk.DEFAULT_VALUES) : Weightable {

    val hit: Set<String>
    val leftPct: Float
    val rightPct: Float

    init {
        val left = this.left.strings[valDir]!!
        val right = this.right.strings[valDir]!!

        val set = HashSet<String>()
        set.addAll(left)
        set.retainAll(right)
        hit = set
        leftPct = if (left.isNotEmpty()) hit.size / left.size.toFloat() else 0F
        rightPct = if (right.isNotEmpty()) hit.size / right.size.toFloat() else 0F;
    }

    override fun getWeight(): Float = (leftPct + rightPct) / 2F

    override fun toString(): String {
        return "[${left.pkgName}(${left.strings[valDir]!!.size})  x  ${right.pkgName}(${right.strings[valDir]!!.size}) = hit.size ]"
    }

}

class StringsHitComparator : IComparator<StringsHit> {
    override fun compare(left: Apk, right: Apk): StringsHit {
        return StringsHit(left, right)
    }
}