package day12

import java.io.File
import kotlin.math.cos

fun Char.distance(other: Char) =
    if (this == 'E') other - 'z'
    else if (this == 'S') other - 'a'
    else if (other == 'E') this - 'z'
    else if (other == 'S') this - 'a'
    else other - this

data class DijkstraParams(
    val dist: MutableMap<Pair<Int, Int>, Distance> = mutableMapOf(),
    val prev: MutableMap<Pair<Int, Int>, Pair<Int, Int>?> = mutableMapOf(),
    val queue: MutableList<Pair<Int, Int>> = mutableListOf()
)

data class Distance(
    val distance: Int = Int.MAX_VALUE
) {

    operator fun plus(cost: Int): Distance =
        if(distance == Int.MAX_VALUE) throw Exception("Cannot sum to infinity")
        else Distance(distance + cost)

    fun isLess(other: Distance) = distance < other.distance

}

data class Node(
    val links: Map<Pair<Int, Int>, Int>
)

data class Graph(
    val nodes: Map<Pair<Int, Int>, Node> = emptyMap(),
    val start: Pair<Int, Int> = Pair(0, 0),
    val end: Pair<Int, Int> = Pair(0, 0)
) {

    fun addNode(x: Int, y: Int, value: Char, vararg links: Triple<Int, Int, Char>?): Graph {
        val newNode = Node(
            links.filterNotNull().fold(emptyMap()) { acc, it -> acc + (Pair(it.first, it.second) to value.distance(it.third))}
        )
        return if (value  == 'S') this.copy(nodes = nodes + (Pair(x,y) to newNode), start = Pair(x, y))
            else if (value == 'E') this.copy(nodes = nodes + (Pair(x,y) to newNode), end = Pair(x, y))
            else this.copy(nodes = nodes + (Pair(x,y) to newNode))
    }

    fun findShortestPathWithDijkstra(): Int {
        val params = DijkstraParams()
        nodes.keys.onEach {
            params.dist[it] = Distance(if (it == start) 0 else Int.MAX_VALUE)
            params.prev[it] = null
            params.queue.add(it)
        }
        while(params.queue.contains(end)) {
            val minVertex = params.dist.filter { params.queue.contains(it.key) }.minBy { it.value.distance }.key
            nodes[minVertex]!!.links.entries.filter { it.value == 1 || it.value == 0 }.onEach {
                if (params.queue.contains(it.key)) {
                    val newDist = params.dist[minVertex]!! + it.value
                    if (newDist.isLess(params.dist[it.key]!!)) {
                        params.dist[it.key] = newDist
                        params.prev[it.key] = minVertex
                    }
                }
            }
            params.queue.remove(minVertex)
        }
        println(params)
        return 0
    }

}

fun parseInputFile() =
    File("src/main/kotlin/day12/input.txt")
        .readLines()
        .fold(emptyList<List<Char>>()) { acc, it ->
            acc + listOf(it.toCharArray().toList())
        }

fun getGraph() =
    parseInputFile()
        .let { rows ->
            (rows.indices).fold(Graph()) { acc, r ->
                (rows[r].indices).fold(acc) { graph, c ->
                    graph.addNode(
                        r, c, rows[r][c],
                        if (r == 0) null else Triple(r-1, c, rows[r-1][c]),
                        if (c == 0) null else Triple(r, c-1, rows[r][c-1]),
                        if (r == rows.size-1) null else Triple(r+1, c, rows[r+1][c]),
                        if (c == rows[r].size-1) null else Triple(r, c+1, rows[r][c+1])
                    )
                }
            }
        }

fun main() {
    val g = getGraph()
    println(g.findShortestPathWithDijkstra())
}