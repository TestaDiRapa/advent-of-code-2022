package day02

import java.io.File
import java.lang.Exception

enum class MoveTypes {
    ROCK,
    PAPER,
    SCISSORS
}

fun getWinningMove(move: Move) = when(move.type) {
    MoveTypes.ROCK -> Move("B")
    MoveTypes.PAPER -> Move("C")
    MoveTypes.SCISSORS -> Move("A")
}

fun getLosingMove(move: Move) = when(move.type) {
    MoveTypes.ROCK -> Move("C")
    MoveTypes.PAPER -> Move("A")
    MoveTypes.SCISSORS -> Move("B")
}


class Move(
    code: String
): Comparable<Move> {

    val type = when(code) {
        "A" -> MoveTypes.ROCK
        "B" -> MoveTypes.PAPER
        "C" -> MoveTypes.SCISSORS
        "X" -> MoveTypes.ROCK
        "Y" -> MoveTypes.PAPER
        "Z" -> MoveTypes.SCISSORS
        else -> throw Exception()
    }
    override fun compareTo(other: Move): Int {
        if(type == other.type) return 0
        return when(type) {
            MoveTypes.ROCK -> { if(other.type == MoveTypes.SCISSORS) return 1 else return -1 }
            MoveTypes.PAPER -> { if(other.type == MoveTypes.ROCK) return 1 else return -1 }
            MoveTypes.SCISSORS -> { if(other.type == MoveTypes.PAPER) return 1 else return -1 }
        }
    }

    fun value(): Int =
        when(type) {
            MoveTypes.ROCK -> 1
            MoveTypes.PAPER -> 2
            MoveTypes.SCISSORS -> 3
        }

}

class Match(
    private val yourMove: Move,
    private val otherMove: Move
) {
    fun score(): Int {
        return if (yourMove < otherMove) yourMove.value()
        else if (yourMove > otherMove) 6 + yourMove.value()
        else 3 + yourMove.value()
    }
}

fun getListOfMatchesFromFile() =
    File("src/main/kotlin/day022/input.txt")
        .readLines()
        .fold(listOf<Pair<String, String>>()) { moves, it ->
            val (opponentMove, yourMove) = it.split(" ")
            moves + Pair(opponentMove, yourMove)
        }

fun interpretPairsAsMatches() =
    getListOfMatchesFromFile()
        .map {
            Match(
                Move(it.second),
                Move(it.first)
            )
        }

fun interpretPairsAsResults() =
    getListOfMatchesFromFile()
        .map {
            when(it.second) {
                "X" -> Match(getLosingMove(Move(it.first)), Move(it.first))
                "Y" -> Match(Move(it.first), Move(it.first))
                "Z" -> Match(getWinningMove(Move(it.first)), Move(it.first))
                else -> throw Exception()
            }
        }

fun getTotalScore() = interpretPairsAsMatches().sumOf { it.score() }

fun getTotalScoreWithOutcome() = interpretPairsAsResults().sumOf { it.score() }

fun main() {
    println("The total score if the second row is the move is: ${getTotalScore()}")
    println("The total score if the second row is the outcome is: ${getTotalScoreWithOutcome()}")
}