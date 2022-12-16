package day15

import java.io.File
import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun manhattanDistance(x1: Long, y1: Long, x2: Long, y2: Long) = abs(x1-x2) + abs(y1-y2)

class LineScan(
    initStart: Long,
    initEnd: Long
) {
    val start = minOf(initStart, initEnd)
    val end = maxOf(initStart, initEnd)

    fun union(other: LineScan): List<LineScan> =
        if(other.start-1 > end || other.end+1 < start) listOf(this, other)
        else listOf(LineScan(
            minOf(other.start, start),
            maxOf(other.end, end)))

    fun union(others: List<LineScan>): List<LineScan> =
        others.fold(Pair(this, emptyList<LineScan>())) { acc, it ->
            val united = acc.first.union(it)
            Pair(
                united.first(),
                acc.second + united.drop(1)
            )
        }.let {listOf(it.first) + it.second    }


}

class SensorScan(
    val x: Long,
    val y: Long,
    private val beaconX: Long,
    private val beaconY: Long
) {
    val radius = manhattanDistance(x, y, beaconX, beaconY)

    fun contains(newX: Long, newY: Long) = manhattanDistance(x, y, newX, newY) <= radius
    fun hasBeacon(newX: Long, newY: Long) = newX == beaconX && newY == beaconY

    fun lineScanAtHeight(height: Long) = LineScan(
        x - (radius - abs(y-height)),
        x + (radius - abs(y-height))
    )

}

fun List<SensorScan>.findXBoundaries() =
    this.fold(Pair(this.first(), this.first())) { acc, scan ->
        Pair(
            scan.takeIf { it.x < acc.first.x } ?: acc.first,
            scan.takeIf { it.x > acc.second.x } ?: acc.second
        )
    }.let { Pair(it.first.x - it.first.radius, it.second.x + it.second.radius) }

fun List<SensorScan>.uniqueExcludedPosition(y: Long) =
    this.filter { abs(y-it.y) <= it.radius }
        .fold(emptyList<LineScan>()) { acc, it ->
            it.lineScanAtHeight(y).union(acc)
        }

fun parseInputFile() =
    File("src/main/kotlin/day15/input.txt")
        .readLines()
        .fold(emptyList<SensorScan>()) { acc, it ->
            Regex("Sensor at x=(-?[0-9]+), y=(-?[0-9]+): closest beacon is at x=(-?[0-9]+), y=(-?[0-9]+)")
                .find(it)!!
                .let{
                    acc + SensorScan(
                        it.groupValues[1].toLong(),
                        it.groupValues[2].toLong(),
                        it.groupValues[3].toLong(),
                        it.groupValues[4].toLong()
                    )
                }
        }

fun findBeaconFreePoints(row: Long) =
    parseInputFile()
        .let { scans ->
            val boundaries = scans.findXBoundaries()
            (boundaries.first  .. boundaries.second).fold(0) { acc, it ->
                if ( scans.any{ scan -> scan.contains(it, row) } && scans.all { scan -> !scan.hasBeacon(it, row) }) acc + 1
                else acc
            }
        }

fun findBeacon(range: Long) =
    parseInputFile()
        .let {scans ->
            (0L .. range).fold(emptyList<Long>()) { acc, y ->
                val lineScan = scans.uniqueExcludedPosition(y)
                if(lineScan.size == 1 && lineScan.first().start <= 0L && lineScan.first().end >= range ) acc
                else if(lineScan.size == 1 && lineScan.first().start > 0L) acc + listOf(y + lineScan.first().start*4000000L)
                else if(lineScan.size == 1 && lineScan.first().end < range) acc + listOf(y + lineScan.first().end*4000000L)
                else acc + listOf(y + (lineScan.minBy { it.end }.end+1)*4000000L)

            }
        }


fun main() {
    measureTimeMillis {
        println("In the row where y=2000000, the  positions cannot contain a beacon are: ${findBeaconFreePoints(10)}")
        val beacons = findBeacon(4000000L)
        if (beacons.size > 1) println("I did not find any beacon")
        else println("The beacon tuning frequency is: ${beacons.first()}")
    }.let {
        println("The whole process took: $it milliseconds")
    }
}