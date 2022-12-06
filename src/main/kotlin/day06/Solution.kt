package day06

import java.io.File
import java.lang.Exception

tailrec fun findMarker(data: String, markerSize: Int, count: Int = 0): Int {
    return if( data.length < markerSize) throw Exception("Data should have at least $markerSize characters")
    else if( data.subSequence(0, markerSize).toSet().size == markerSize) count + markerSize
    else findMarker(data.substring(1, data.length), markerSize, count + 1)
}

fun countCharactersUntilMarker() =
    File("src/main/kotlin/day06/input.txt")
        .readText()
        .let {
            findMarker(it, 4)
        }

fun countCharactersUntilMessage() =
    File("src/main/kotlin/day06/input.txt")
        .readText()
        .let {
            findMarker(it, 14)
        }

fun main() {
    println("The number of character up to the first marker is: ${countCharactersUntilMarker()}")
    println("The number of character up to the first message is: ${countCharactersUntilMessage()}")
}