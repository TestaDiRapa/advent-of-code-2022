package day04

import java.io.File

fun idBoundariesToSet(boundaries: String): Set<Int> {
    val (lower, upper) = boundaries.split('-')
    return (lower.toInt() .. upper.toInt()).toSet()
}

fun getItemsFromFile() =
    File("src/main/kotlin/day04/input.txt")
        .readLines()
        .fold(listOf<Pair<Set<Int>, Set<Int>>>()) { acc, it ->
            val (firstIds, secondIds) = it.split(',')
            acc + Pair(
                idBoundariesToSet(firstIds),
                idBoundariesToSet(secondIds)
            )
        }

fun getCompletelyOverlappingSets() =
    getItemsFromFile()
        .fold(0) { acc, it ->
            if (it.first.containsAll(it.second) || it.second.containsAll(it.first)) acc + 1
            else acc
        }

fun getOverlappingSets() =
    getItemsFromFile()
        .fold(0) { acc, it ->
            if (it.first.intersect(it.second).isNotEmpty()) acc + 1
            else acc
        }

fun main() {
    println("The assignment ranges fully contained in each other are: ${getCompletelyOverlappingSets()}")
    println("The number of assignments that partially overlap are: ${getOverlappingSets()}")
}