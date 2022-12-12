package day11

import java.io.File
import java.lang.Exception

data class Monkey(
    val id: Int,
    val items: List<Long>,
    val operation: (Long) -> Long,
    val nextMonkey: (Long) -> Int,
    val monkeyActivity: Int = 0
) {

    fun doATurn(monkeys: List<Monkey>, reduceLevel: Int = 1, verbose: Boolean = false): List<Monkey> {
        if (items.isEmpty()) return monkeys
        val updatedMonkeys = items.fold(monkeys) { acc, item ->
            if (verbose) println("Monkey $id inspects an item with a worry level of $item.")
            val newLevel = operation(item)
            if (verbose) println("\tWorry level is now $newLevel.")
            val dividedLevel = newLevel/reduceLevel.toLong()
            if (verbose) println("\tMonkey gets bored with item. Worry level is divided by 3 to $dividedLevel.")
            val receiverMonkeyId = nextMonkey(dividedLevel)
            if (verbose) println("\tItem with worry level $dividedLevel is thrown to monkey $receiverMonkeyId.")
            val receiverMonkey = acc.first { it.id == receiverMonkeyId }

            acc.filter { it.id != receiverMonkeyId } + receiverMonkey.copy(
                items = receiverMonkey.items + dividedLevel
            )
        }
        return updatedMonkeys.filter { it.id != id } +
                this.copy(items = emptyList(), monkeyActivity = monkeyActivity + items.size)
    }

}

fun parseNextMonkey(input: String): (Long) -> Int {
    val divisor = Regex("Test: divisible by ([0-9]+)").find(input)!!.groupValues[1].toInt()
    val ifTrue = Regex("If true: throw to monkey ([0-9]+)").find(input)!!.groupValues[1].toInt()
    val ifFalse = Regex("If false: throw to monkey ([0-9]+)").find(input)!!.groupValues[1].toInt()
    return { it -> if (it%divisor.toLong() == 0.toLong()) ifTrue else ifFalse}
}

fun parseOperation(rawOperation: String): (Long) -> Long {
    Regex("new = ([0-9a-z]+) ([*+-]) ([0-9a-z]+)")
        .find(rawOperation)!!
        .groupValues
        .let { groups ->
            val first = groups[1]
            val op = groups[2]
            val second = groups[3]
            return if(first == "old" && second == "old") {
                when(op) {
                    "+" -> { it -> it + it }
                    "*" -> { it -> it * it }
                    else -> throw Exception("Operation not supported")
                }
            } else {
                when(op) {
                    "+" -> { it -> it + second.toLong() }
                    "*" -> { it -> it * second.toLong() }
                    else -> throw Exception("Operation not supported")
                }
            }
        }
}

fun parseInputFile() =
    File("src/main/kotlin/day11/input.txt")
        .readText()
        .split(Regex("\r\n\r\n"))
        .fold(emptyList<Monkey>()) { acc, rawMonkey ->
            val monkeyId = Regex("Monkey ([0-9]+)").find(rawMonkey)!!.groupValues[1].toInt()
            val items = Regex("[0-9]+").findAll(
                Regex("Starting items: ([0-9]+,? ?)+").find(rawMonkey)!!.value
            ).toList().map { it.value.toLong() }
            val operation = parseOperation(
                Regex("Operation: new = [a-z0-9]+ [*+-] [a-z0-9]+").find(rawMonkey)!!.value
            )
            val nextMonkey = parseNextMonkey(rawMonkey)
            acc + Monkey(
                monkeyId,
                items,
                operation,
                nextMonkey
            )
        }

fun findMonkeyBusinessAfterNthRound(round: Int, worryLevel: Int): Int {
    val monkeys = parseInputFile()
    val ids = (monkeys.indices)
    val finalMonkeys = (0 until round).fold(monkeys) { m, _ ->
       ids.fold(m) { acc, id ->
            val monkey = acc.first { it.id == id }
            monkey.doATurn(acc, worryLevel)
        }
    }
    val monkeyActivity = finalMonkeys.map { it.monkeyActivity }.sortedDescending()
    return monkeyActivity[0] * monkeyActivity[1]
}

fun main() {
    println("The level of monkey business after 20 rounds is ${findMonkeyBusinessAfterNthRound(20, 3)}")
}