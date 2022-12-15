package day13

import java.io.File

abstract class Node : Comparable<Node>

class IntNode(
    val value: Int
): Node() {
    override fun compareTo(other: Node): Int =
        if (other is IntNode) this.value.compareTo(other.value)
        else ListNode(listOf(this)).compareTo(other)
}

class ListNode(
    val children: List<Node>
): Node() {
    override fun compareTo(other: Node): Int =
        if(other is IntNode) this.compareTo(ListNode(listOf(other)))
        else {
            if (this.children.isEmpty() && (other as ListNode).children.isEmpty()) 0
            else if (this.children.isEmpty()) -1
            else if ((other as ListNode).children.isEmpty()) 1
            else if (this.children.first().compareTo(other.children.first()) == 0)
                ListNode(this.children.drop(1)).compareTo(ListNode(other.children.drop(1)))
            else this.children.first().compareTo(other.children.first())
        }
}

//fun compareTrees(tree1: Node, tree2: Node): Int =
//    if(tree1 is IntNode && tree2 is IntNode) tree1.value.compareTo(tree2.value)
//    else if (tree1 is ListNode && tree2 is IntNode) compareTrees(tree1, ListNode(listOf(tree2)))
//    else if (tree1 is IntNode && tree2 is ListNode) compareTrees(ListNode(listOf(tree1)), tree2)
//    else {
//        val l1 = tree1 as ListNode
//        val l2 = tree2 as ListNode
//        if (l1.children.isEmpty() && l2.children.isEmpty()) 0
//        else if (l1.children.isEmpty()) -1
//        else if (l2.children.isEmpty()) 1
//        else if (compareTrees(l1.children.first(), l2.children.first()) == 0)
//            compareTrees(ListNode(l1.children.drop(1)), ListNode(l2.children.drop(1)))
//        else compareTrees(l1.children.first(), l2.children.first())
//    }


tailrec fun parseListContent(rawContent: String, innerLists: List<Char> = emptyList(), parsed: List<String> = emptyList()): List<String> =
    if (rawContent.isEmpty()) parsed
    else if(rawContent.startsWith("[")) parseListContent(
        rawContent.substring(1, rawContent.length),
        innerLists + '[',
        if (innerLists.isEmpty())
            parsed + listOf("[")
        else
            parsed.dropLast(1) + listOf(parsed.last() + '[')
    )
    else if(innerLists.isEmpty() && rawContent.startsWith(",")) parseListContent(
        rawContent.substring(1, rawContent.length),
        innerLists,
        parsed)
    else if(innerLists.isEmpty() && rawContent.matches(Regex("^[0-9]+.*"))) parseListContent(
        rawContent.replace(Regex("^[0-9]+"), ""),
        innerLists,
        parsed + listOf(Regex("^[0-9]+").find(rawContent)!!.value))
    else if(rawContent.startsWith("]")) parseListContent(
        rawContent.substring(1, rawContent.length),
        innerLists.dropLast(1),
        parsed.dropLast(1) + listOf(parsed.last() + "]"))
    else parseListContent(
        rawContent.substring(1, rawContent.length),
        innerLists,
        parsed.dropLast(1) + listOf(parsed.last() + rawContent[0]))

fun parseTree(rawString: String): Node =
    if(rawString.first() == '[' && rawString.last() == ']') {
        val rawParams = parseListContent(rawString.substring(1, rawString.length-1))
        val nodes = rawParams.map { parseTree(it) }
        ListNode(nodes)
    } else if (rawString.matches(Regex("^[0-9]+$"))) IntNode(rawString.toInt())
    else throw Exception("Invalid state")

fun parseInputFile() =
    File("src/main/kotlin/day13/input.txt")
        .readText()
        .split(Regex("\r?\n"))
        .filter{ it.isNotBlank() }
        .fold(emptyList<Node>()) { acc, it ->
            acc + parseTree(it)
        }

fun findSumOfIndicesInRightOrder() =
    parseInputFile()
        .chunked(2)
        .foldIndexed(0) { index, acc, it ->
            if(it[0] < it[1]) acc + index + 1
            else acc
        }

fun findDecoderKey() =
    parseInputFile().let{
        val dividerStart = ListNode(listOf(ListNode(listOf(IntNode(2)))))
        val dividerEnd = ListNode(listOf(ListNode(listOf(IntNode(6)))))
        val sortedList = (it + listOf(dividerStart, dividerEnd)).sorted()
        sortedList.foldIndexed(Pair(0, 0)) { index, acc, node ->
            Pair(
                index.takeIf { node.compareTo(dividerStart) == 0 } ?: acc.first,
                index.takeIf { node.compareTo(dividerEnd) == 0 } ?: acc.second
            )
        }.let { p -> (p.first+1) * (p.second+1) }
    }


fun main() {
    println("The sum of the indices of the ordered pairs is ${findSumOfIndicesInRightOrder()}")
    println("The decoder key for the distress signal is ${findDecoderKey()}")
}