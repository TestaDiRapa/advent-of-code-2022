package day05

import java.io.File
import java.util.*

data class Move(
    val qty: Int,
    val source: String,
    val dest: String
)

fun parseStack(rawInput: List<String>): Map<String, Stack<Char>> =
    (1 .. 9).fold(emptyMap()) { acc, it ->
        acc + (it.toString() to rawInput
            .last()
            .indexOf(it.toString())
            .let { index ->
                rawInput
                    .reversed()
                    .subList(1, rawInput.size)
                    .fold(Stack<Char>()) { stack, line ->
                        if (line.length > index && line[index] != ' ')
                            stack.push(line[index])
                        stack
                    }
            })
    }

fun parseCommands(rawCommands: List<String>) =
    rawCommands.mapNotNull { line ->
        Regex("move ([0-9]+) from ([0-9]+) to ([0-9]+)")
            .find(line)
            ?.groups
            ?.toList()
            ?.let {
                Move(
                    it[1]!!.value.toInt(),
                    it[2]!!.value,
                    it[3]!!.value
                )
            }
    }

fun parseInputFile() =
    File("src/main/kotlin/day05/input.txt")
        .readLines()
        .fold(Pair<List<String>, List<String>>(emptyList(), emptyList())) { acc, line ->
            if (line.startsWith("move")) Pair(acc.first, acc.second + line)
            else if (line.isEmpty()) acc
            else Pair(acc.first + line, acc.second)
        }.let {
            Pair(
                parseStack(it.first),
                parseCommands(it.second)
            )

        }

fun findFinalConfigurationMovingOneCrateAtTime() =
    parseInputFile()
        .let { input ->
            input.second.forEach {move ->
                repeat((1 .. move.qty).count()) {
                    if (input.first[move.source]!!.isNotEmpty())
                        input.first[move.dest]!!.push(input.first[move.source]!!.pop())
                }
            }
            (1..9).fold("") { acc, it ->
                acc + input.first[it.toString()]!!.peek()
            }
        }

fun findFinalConfigurationMovingMultipleCratesAtTime() =
    parseInputFile()
        .let { input ->
            input.second.forEach { move ->
                (1 .. move.qty).fold(emptyList<Char>()) { acc, _ ->
                    if (input.first[move.source]!!.isNotEmpty())
                        acc + input.first[move.source]!!.pop()
                    else acc
                }.reversed().forEach { input.first[move.dest]!!.push(it) }
            }
            (1..9).fold("") { acc, it ->
                acc + input.first[it.toString()]!!.peek()
            }
        }

fun main() {
    println("The top crates at the end using the CrateMover 9000 are: ${findFinalConfigurationMovingOneCrateAtTime()}")
    println("The top crates at the end using the CrateMover 9001 are: ${findFinalConfigurationMovingMultipleCratesAtTime()}")
}