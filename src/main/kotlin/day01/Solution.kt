package day01

import java.io.File

fun findTotalCaloriesPerElf() =
    File("src/main/kotlin/day01/input.txt")
        .readText()
        .split(Regex("\n\n"))
        .fold(listOf<Int>()) { currentList, newElfCalories ->
            currentList + newElfCalories.split(Regex("\n"))
                .filter { it.isNotBlank() }
                .fold(0) { acc, it -> acc + it.toInt()  }
        }.sortedDescending().toList()
fun findElfCarryingMostCalories() = findTotalCaloriesPerElf().first()

fun findTopThreeElvesCarryingMostCalories() = findTotalCaloriesPerElf().subList(0,3).sum()

fun main() {
    println("The elf carrying most calories is carrying: ${findElfCarryingMostCalories()}")
    println("The top 3 elves carrying most calories are carrying: ${findTopThreeElvesCarryingMostCalories()}")
}