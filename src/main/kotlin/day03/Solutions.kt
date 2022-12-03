package day03

import java.io.File

fun getItemsPriorities() =
    ('a'..'z').foldIndexed(mapOf<Char, Int>()) { index, acc, c ->
        acc + (c to index+1)
    } + ('A' .. 'Z').foldIndexed(mapOf()) { index, acc, c ->
        acc + (c to index + 27)
    }

fun getItemsFromFile() =
    File("src/main/kotlin/day03/input.txt")
        .readLines()
        .fold(listOf<Pair<List<Char>, List<Char>>>()) { acc, line ->
            acc + Pair(
                line.subSequence(0, line.length/2).toList(),
                line.subSequence(line.length/2, line.length).toList()
            )
        }

fun findCommonItemAndSumPriorities() =
    getItemsFromFile()
        .fold(0) { acc, items ->
            (items.first.toSet().intersect(items.second.toSet())
                .firstOrNull()
                ?.let { getItemsPriorities()[it] } ?: 0) + acc
        }

fun findBadgesAndSumPriorities() =
    getItemsFromFile()
        .chunked(3)
        .fold(0) { acc, group ->
            (group.fold(emptySet<Char>()) { intersected, backpack ->
                intersected.ifEmpty {
                    backpack.first.toSet() + backpack.second.toSet()
                }.intersect(
                    backpack.first.toSet() + backpack.second.toSet()
                )
            }.firstOrNull()
                ?.let { getItemsPriorities()[it] } ?: 0) + acc
        }

fun main() {
    println("The sum of the priorities of the common items in the compartments is: ${findCommonItemAndSumPriorities()}")
    println("The sum of the priorities of the group badges is: ${findBadgesAndSumPriorities()}")
}