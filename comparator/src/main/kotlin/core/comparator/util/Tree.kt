package core.comparator.util

/**
 * @author DrkCore
 * @since 10/30/18
 */
class Tree<T> {

    val root = Node<T>()

    interface Filter<T> {
        fun accept(node: Node<T>): Boolean;
    }

    fun dump(filter: Filter<T>): List<Node<T>> {
        return dump(root, null, filter)
    }

    private fun dump(node: Node<T>, list: MutableList<Node<T>>? = null, filter: Filter<T>? = null): List<Node<T>> {
        var result = list
        if (result == null) {
            result = mutableListOf()
        }

        if (filter == null || filter.accept(node)) {
            result.add(node)
        }

        for (child in node.child.values) {
            dump(child, result, filter)
        }

        return result
    }

}