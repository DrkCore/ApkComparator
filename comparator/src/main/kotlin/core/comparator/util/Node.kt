package core.comparator.util

/**
 * @author DrkCore
 * @since 10/30/18
 */
class Node<T>(val name: String = "", var content: T? = null, val parent: Node<T>? = null) {

    val child = mutableMapOf<String, Node<T>>()

    fun isEmpty(): Boolean = child.isEmpty()

    fun getOrCreateChild(name: String, content: T? = null): Node<T> {
        var node = child[name]
        if (node == null) {
            node = Node(name, content, this)
            child[name] = node
        }
        return node
    }

    fun getChild(name: String): Node<T>? {
        return child[name]
    }

    fun getPaths(ignoreEmpty: Boolean = true): List<String> {
        var node: Node<T>? = this
        val paths = mutableListOf<String>()
        do {
            if (node!!.name.isNotEmpty() || !ignoreEmpty) {
                paths.add(node.name)
            }
            node = node.parent
        } while (node != null)
        paths.reverse()
        return paths
    }

    fun getFullPath(separator: String, ignoreEmpty: Boolean = true): String {
        var path = ""

        for (node in getPaths(ignoreEmpty)) {
            path += node + separator
        }

        return if (path.isNotEmpty()) path.substring(0, path.length - 1) else path
    }
}