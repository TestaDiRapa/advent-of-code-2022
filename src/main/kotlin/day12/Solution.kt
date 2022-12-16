package day12

import java.io.File

fun Char.distance(other: Char) =
    if (this == 'E') other - 'z'
    else if (this == 'S') other - 'a'
    else if (other == 'E') 'z' - this
    else if (other == 'S') 'a' - this
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
    val height: Char,
    val links: Map<Pair<Int, Int>, Int>
) {

    fun distance(node: Node) = height.distance(node.height)

}

data class Graph(
    val nodes: Map<Pair<Int, Int>, Node> = emptyMap(),
    val start: Pair<Int, Int> = Pair(0, 0),
    val end: Pair<Int, Int> = Pair(0, 0)
) {

    fun addNode(x: Int, y: Int, value: Char, vararg links: Triple<Int, Int, Char>?): Graph {
        val newNode = Node(
            when (value) { 'S' -> 'a' 'E' -> 'z' else -> value},
            links
                .filterNotNull()
                .fold(emptyMap()) { acc, it -> acc + (Pair(it.first, it.second) to 1)}
        )
        return when(value) {
            'S' -> this.copy(nodes = nodes + (Pair(x,y) to newNode), start = Pair(x, y))
            'E' -> this.copy(nodes = nodes + (Pair(x,y) to newNode), end = Pair(x, y))
            else -> this.copy(nodes = nodes + (Pair(x,y) to newNode))
        }
    }

    private tailrec fun stepsToStart(end: Pair<Int, Int>, nodes: Map<Pair<Int, Int>, Pair<Int, Int>?>, acc: Int = 0): Int =
        if(nodes[end] != null) stepsToStart(nodes[end]!!, nodes, acc+1)
        else acc

    fun findShortestPathWithDijkstra(): Int {
        val params = DijkstraParams()
        nodes.keys.onEach {
            params.dist[it] = Distance(if (it == start) 0 else Int.MAX_VALUE)
            params.prev[it] = null
            params.queue.add(it)
        }
        while(params.queue.contains(end)) {
            val minVertex = params.dist.filter { params.queue.contains(it.key) }.minBy { it.value.distance }.key
            nodes[minVertex]!!.links.filter { nodes[minVertex]!!.distance(nodes[it.key]!!) <= 1 }.entries.onEach {
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
        return stepsToStart(end, params.prev)
    }

    fun findShortestPathWithBFS(): Int {
        val queue = mutableListOf(Pair(end,0))
        val explored = mutableSetOf(end)
        var current = queue.first()
        while(queue.isNotEmpty() && nodes[queue.first().first]!!.height != 'a') {
            current = queue.first()
            val validLinks = nodes[current.first]!!.links.keys
                .filter { !explored.contains(it) && nodes[it]!!.distance(nodes[current.first]!!) <= 1 }
            queue.addAll(validLinks.map { Pair(it, current.second+1) })
            explored.addAll(validLinks)
            queue.removeAt(0)
        }
        return current.second
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
    println("The shortest path is made up of ${getGraph().findShortestPathWithDijkstra()} steps")
    println("The shortest hiking path is made up of ${getGraph().findShortestPathWithBFS()} steps")
}