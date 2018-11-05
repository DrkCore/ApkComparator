package core.comparator.compare

/**
 * @author DrkCore
 * @since 11/5/18
 */
interface IDepthLimit {

    companion object {
        const val DEFAULT_DEPTH = 4
    }

    var depth: Int
}