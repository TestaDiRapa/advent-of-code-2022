package day08

import java.io.File

fun getVerticalHeightMap(matrix: Array<Array<Int>>) {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (0 .. matrix.size).onEach {
        
    }
}

fun getHorizontalHeightMap(matrix: List<List<Int>>) =
    (0 .. matrix.first().size).fold() {

    }

fun parseInputFile() =
    File("src/main/kotlin/day07/input.txt")
        .readLines()
        .let{ lines ->
            Array(lines.size) {
                lines[it].asSequence().map { it.toString().toInt() }.toList().toTypedArray()
            }
        }

