package day07

import java.io.File

data class DirectoryTree (
    val name: String,
    val size: Int = 0,
    val children: List<DirectoryTree> = listOf()
) {

    val fullSize: Int by lazy { size + children.sumOf { it.fullSize } }

    fun getDirectorySizeList(): List<Int> =
        if (size == 0) {
            listOf(fullSize) + children.flatMap { it.getDirectorySizeList() }
        } else emptyList()

}

fun sumDirectoriesUnderSize(tree: DirectoryTree, maxSize: Int): Int
    = (tree.fullSize.takeIf { it <= maxSize } ?: 0) + tree.children.filter { it.size == 0 }.sumOf { sumDirectoriesUnderSize(it, maxSize) }

fun findSmallerDirectoryToDelete(tree: DirectoryTree): Int {
    val availableSpace = 70000000 - tree.fullSize
    val neededSpace = 30000000 - availableSpace
    assert(neededSpace > 0)

    return tree.getDirectorySizeList().sorted().first { it >= neededSpace }
}

fun addToTreeAtNode(tree: DirectoryTree, levels: List<String>, size: Int, name: String): DirectoryTree {
    return if (levels.isEmpty()) {
        tree.copy(children = tree.children + DirectoryTree(name, size))
    } else {
        val nextLevel = levels.first()
        val nextChild = tree.children.first { it.name == nextLevel }
        tree.copy(children =
            tree.children.filter{ it.name != nextLevel} + addToTreeAtNode(nextChild, levels.drop(1), size, name)
            )
    }
}

fun parseFileAndBuildTree() =
    File("src/main/kotlin/day07/input.txt")
        .readLines()
        .fold(Pair(emptyList<String>(), DirectoryTree("/"))) { acc, it ->
            if ( it.startsWith("$")) {
                Regex("cd ([.a-z]+)").find(it)
                    ?.groupValues
                    ?.let {
                        if (it[1] == "..") {
                            Pair(
                                acc.first.dropLast(1),
                                acc.second
                            )
                        } else {
                            Pair(
                                acc.first + it[1],
                                acc.second
                            )
                        }
                    }?: acc
            } else {
                val (sizeType, fileName) = it.split(' ')
                Pair(
                    acc.first,
                    addToTreeAtNode(
                        acc.second,
                        acc.first,
                        if (sizeType == "dir") 0 else sizeType.toInt(),
                        fileName
                    )
                )
            }
        }.second

fun main() {
    val tree = parseFileAndBuildTree()
    println("The sum of the directory which size is under 100000 is ${sumDirectoriesUnderSize(tree, 100000)}")
    println("The size of the smallest directory to delete is ${findSmallerDirectoryToDelete(tree)}")
}