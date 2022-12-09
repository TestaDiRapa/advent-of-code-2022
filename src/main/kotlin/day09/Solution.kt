package day09

import java.io.File
import kotlin.math.abs

data class Position(
    val x: Int = 0,
    val y: Int = 0
) {

    fun doAStep(direction: Direction): Position {
        return when (direction) {
            Direction.UP -> this.copy(y = y+1)
            Direction.DOWN -> this.copy(y = y-1)
            Direction.LEFT -> this.copy(x = x-1)
            Direction.RIGHT -> this.copy(x = x+1)
        }
    }

    fun follow(position: Position): Position =
        if ( abs(x - position.x) <= 1 && abs(y - position.y) <= 1) this
        else if ( y == position.y) this.copy(x = x + if (position.x > x) 1 else -1)
        else if ( x == position.x) this.copy(y = y + if (position.y > y) 1 else -1)
        else this.copy(x = x + if (position.x > x) 1 else -1, y = y + if (position.y > y) 1 else -1)
}

class Rope(
    length: Int = 0,
    nodes: List<Position> = emptyList()
) {
    private val nodes = nodes.ifEmpty { List(length) { Position() } }


    private fun updateRecursive(headPosition: Position, nodes: List<Position>): List<Position> {
        return if(nodes.isEmpty()) emptyList()
        else {
            val newHead = nodes.first().follow(headPosition)
            listOf(newHead) + updateRecursive(newHead, nodes.subList(1, nodes.size))
        }
    }

    fun doAStep(direction: Direction): Rope {
        val newHead = nodes.first().doAStep(direction)
        return Rope(
            nodes = listOf(newHead) + updateRecursive(newHead, nodes.subList(1, nodes.size))
        )
    }

    fun getTailPosition() = nodes.last()

}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;
    companion object {
        fun from(s: String): Direction {
            return when (s) {
                "U" -> UP
                "D" -> DOWN
                "L" -> LEFT
                "R" -> RIGHT
                else -> throw Exception()
            }
        }
    }
}

fun parseTailMovementAndCountPositions(size: Int) =
    File("src/main/kotlin/day09/input.txt")
        .readLines()
        .fold(Pair(Rope(size), setOf(Position()))) { acc, line ->
            val (d, amount) = line.split(" ")
            val direction = Direction.from(d)
            (0 until amount.toInt()).fold(acc) { innerAcc, _ ->
                val newRope = innerAcc.first.doAStep(direction)
                Pair(
                    newRope,
                    innerAcc.second + newRope.getTailPosition()
                )
            }
        }.second.size

fun main() {
    println("The number of positions the short rope visited at least once is: ${parseTailMovementAndCountPositions(2)}")
    println("The number of positions the long rope visited at least once is: ${parseTailMovementAndCountPositions(10)}")
}