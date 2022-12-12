package day11

import java.io.File
import java.lang.Exception

class IntModule(
    initial: Int = 0,
    oldModuleMap: Map<Int, Int>? = null
) {
    private val moduleMap = oldModuleMap ?: listOf(2,3,5,7,11,13,17,19,23).fold(emptyMap()) { acc, it -> acc + (it to initial%it)}

    operator fun plus(increment: Int): IntModule =
        moduleMap.keys.fold(emptyMap<Int, Int>()) { acc, it ->
            acc + (it to (moduleMap[it]!! + increment)%it)
        }.let {
            IntModule(oldModuleMap = it)
        }

    operator fun times(increment: Int): IntModule =
        moduleMap.keys.fold(emptyMap<Int, Int>()) { acc, it ->
            acc + (it to (moduleMap[it]!! * increment)%it)
        }.let {
            IntModule(oldModuleMap = it)
        }

    fun squared(): IntModule =
        moduleMap.keys.fold(emptyMap<Int, Int>()) { acc, it ->
            acc + (it to (moduleMap[it]!! * moduleMap[it]!!)%it)
        }.let {
            IntModule(oldModuleMap = it)
        }

    fun isDivisibleBy(other: Int): Boolean = moduleMap[other] == 0

}

data class MonkeyModule(
    val id: Int,
    val items: List<IntModule>,
    val operation: (IntModule) -> IntModule,
    val nextMonkey: (IntModule) -> Int,
    val monkeyActivity: Long = 0
) {

    fun doATurn(monkeys: List<MonkeyModule>, verbose: Boolean = false): List<MonkeyModule> {
        if (items.isEmpty()) return monkeys
        val updatedMonkeys = items.fold(monkeys) { acc, item ->
            if (verbose) println("Monkey $id inspects an item with a worry level of $item.")
            val newLevel = operation(item)
            if (verbose) println("\tWorry level is now $newLevel.")
            val receiverMonkeyId = nextMonkey(newLevel)
            if (verbose) println("\tItem with worry level $newLevel is thrown to monkey $receiverMonkeyId.")
            val receiverMonkey = acc.first { it.id == receiverMonkeyId }

            acc.filter { it.id != receiverMonkeyId } + receiverMonkey.copy(
                items = receiverMonkey.items + newLevel
            )
        }
        return updatedMonkeys.filter { it.id != id } +
                this.copy(items = emptyList(), monkeyActivity = monkeyActivity + items.size)
    }

}

fun parseNextMonkeyWithModule(input: String): (IntModule) -> Int {
    val divisor = Regex("Test: divisible by ([0-9]+)").find(input)!!.groupValues[1].toInt()
    val ifTrue = Regex("If true: throw to monkey ([0-9]+)").find(input)!!.groupValues[1].toInt()
    val ifFalse = Regex("If false: throw to monkey ([0-9]+)").find(input)!!.groupValues[1].toInt()
    return { it -> if (it.isDivisibleBy(divisor)) ifTrue else ifFalse}
}

fun parseOperationWithModule(rawOperation: String): (IntModule) -> IntModule {
    Regex("new = ([0-9a-z]+) ([*+-]) ([0-9a-z]+)")
        .find(rawOperation)!!
        .groupValues
        .let { groups ->
            val first = groups[1]
            val op = groups[2]
            val second = groups[3]
            return if(first == "old" && second == "old") {
                when(op) {
                    "+" -> { it -> it * 2 }
                    "*" -> { it -> it.squared() }
                    else -> throw Exception("Operation not supported")
                }
            } else {
                when(op) {
                    "+" -> { it -> it + second.toInt() }
                    "*" -> { it -> it * second.toInt() }
                    else -> throw Exception("Operation not supported")
                }
            }
        }
}

fun parseInputFileWithModule() =
    File("src/main/kotlin/day11/input.txt")
        .readText()
        .split(Regex("\r?\n\r?\n"))
        .fold(emptyList<MonkeyModule>()) { acc, rawMonkey ->
            val monkeyId = Regex("Monkey ([0-9]+)").find(rawMonkey)!!.groupValues[1].toInt()
            val items = Regex("[0-9]+").findAll(
                Regex("Starting items: ([0-9]+,? ?)+").find(rawMonkey)!!.value
            ).toList().map { IntModule(it.value.toInt()) }
            val operation = parseOperationWithModule(
                Regex("Operation: new = [a-z0-9]+ [*+-] [a-z0-9]+").find(rawMonkey)!!.value
            )
            val nextMonkey = parseNextMonkeyWithModule(rawMonkey)
            acc + MonkeyModule(
                monkeyId,
                items,
                operation,
                nextMonkey
            )
        }

fun findMonkeyBusinessAfterNthRound(rounds: Int): Long {
    val monkeys = parseInputFileWithModule()
    val ids = (monkeys.indices)
    val finalMonkeys = (0 until rounds).fold(monkeys) { m, _ ->
       ids.fold(m) { acc, id ->
            val monkey = acc.first { it.id == id }
            monkey.doATurn(acc)
        }
    }
    val monkeyActivity = finalMonkeys.map { it.monkeyActivity }.sortedDescending()
    return monkeyActivity[0] * monkeyActivity[1]
}

fun main() {
    println("The level of monkey business after 10000 rounds is ${findMonkeyBusinessAfterNthRound(10000)}")
}