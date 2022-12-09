package day08

import java.io.File
import kotlin.math.max

fun getViewFromNorth(matrix: Array<Array<Int>>): Array<Array<Int>> {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (matrix.indices).onEach { i ->
        (0 until matrix[0].size).onEach { j ->
            newMatrix[i][j] =
                if (i == 0) matrix[0][j]
                else if (newMatrix[i-1][j] > matrix[i][j]) newMatrix[i-1][j]
                else matrix[i][j]
        }
    }
    return newMatrix
}

fun getViewFieldFromNorth(matrix: Array<Array<Int>>): Array<Array<Int>> {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (0 until matrix[0].size).onEach { j ->
        val distanceCache = MutableList<Int?>(10) { null }
        (matrix.indices).onEach { i ->
            newMatrix[i][j] = i -
                    distanceCache.indices
                    .fold(0) { acc, idx -> if (idx >= matrix[i][j] && distanceCache[idx] != null && distanceCache[idx]!! > acc) distanceCache[idx]!! else acc}
            distanceCache[matrix[i][j]] = i
        }
    }
    return newMatrix
}

fun getViewFromWest(matrix: Array<Array<Int>>): Array<Array<Int>> {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (matrix.indices).onEach { i ->
        (0 until matrix[0].size).onEach { j ->
            newMatrix[i][j] =
                if (j == 0) matrix[i][j]
                else if (newMatrix[i][j-1] > matrix[i][j]) newMatrix[i][j-1]
                else matrix[i][j]
        }
    }
    return newMatrix
}

fun getViewFieldFromWest(matrix: Array<Array<Int>>): Array<Array<Int>> {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (matrix.indices).onEach { i ->
        val distanceCache = MutableList<Int?>(10) { null }
        (0 until matrix[0].size).onEach { j ->
            newMatrix[i][j] = j -
                    distanceCache.indices
                        .fold(0) { acc, idx -> if (idx >= matrix[i][j] && distanceCache[idx] != null && distanceCache[idx]!! > acc) distanceCache[idx]!! else acc}
            distanceCache[matrix[i][j]] = j
        }
    }
    return newMatrix
}

fun getViewFromEast(matrix: Array<Array<Int>>): Array<Array<Int>> {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (matrix.indices).onEach { i ->
        (0 until matrix[0].size).reversed().onEach { j ->
            newMatrix[i][j] =
                if (j == matrix[0].size-1) matrix[i][j]
                else if (newMatrix[i][j+1] > matrix[i][j]) newMatrix[i][j+1]
                else matrix[i][j]
        }
    }
    return newMatrix
}

fun getViewFieldFromEast(matrix: Array<Array<Int>>): Array<Array<Int>> {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (matrix.indices).onEach { i ->
        val distanceCache = MutableList<Int?>(10) { null }
        (0 until matrix[0].size).reversed().onEach { j ->
            newMatrix[i][j] = - j + distanceCache.indices
                        .fold(matrix[0].size-1) { acc, idx -> if (idx >= matrix[i][j] && distanceCache[idx] != null && distanceCache[idx]!! < acc) distanceCache[idx]!! else acc}

            distanceCache[matrix[i][j]] = j
        }
    }
    return newMatrix
}

fun getViewFromSouth(matrix: Array<Array<Int>>): Array<Array<Int>> {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (matrix.indices).reversed().onEach { i ->
        (0 until matrix[0].size).onEach { j ->
            newMatrix[i][j] =
                if (i == matrix.size-1) matrix[i][j]
                else if (newMatrix[i+1][j] > matrix[i][j]) newMatrix[i+1][j]
                else matrix[i][j]
        }
    }
    return newMatrix
}

fun getViewFieldFromSouth(matrix: Array<Array<Int>>): Array<Array<Int>> {
    val newMatrix = Array(matrix.size) { Array(matrix[0].size) { 0 } }
    (0 until matrix[0].size).onEach { j ->
        val distanceCache = MutableList<Int?>(10) { null }
        (matrix.indices).reversed().onEach { i ->
            newMatrix[i][j] = -i + distanceCache.indices
                .fold(matrix.size-1) { acc, idx -> if (idx >= matrix[i][j] && distanceCache[idx] != null && distanceCache[idx]!! < acc) distanceCache[idx]!! else acc}

            distanceCache[matrix[i][j]] = i
        }
    }
    return newMatrix
}

fun countVisibleTrees(): Int {
    val forest = parseInputFile()
    val northView = getViewFromNorth(forest)
    val southView = getViewFromSouth(forest)
    val eastView = getViewFromEast(forest)
    val westView = getViewFromWest(forest)
    return (forest.indices).fold(0) { acc, row ->
        acc + (0 until forest[0].size).fold(0) { innAcc, col ->
            if (row == 0 || col == 0 || row == forest.size-1 || col == forest[0].size-1) innAcc + 1
            else if (northView[row-1][col] >= forest[row][col] &&
                southView[row+1][col] >= forest[row][col] &&
                westView[row][col-1] >= forest[row][col] &&
                eastView[row][col+1] >= forest[row][col]) innAcc
            else innAcc + 1
        }
    }
}

fun calculateViewField(): Int {
    val forest = parseInputFile()
    val northView = getViewFieldFromNorth(forest)
    val southView = getViewFieldFromSouth(forest)
    val eastView = getViewFieldFromEast(forest)
    val westView = getViewFieldFromWest(forest)
    return (forest.indices).fold(0) { acc, row ->
        max((0 until forest[0].size).fold(0) { innAcc, col ->
            val newVal = northView[row][col] * southView[row][col] * eastView[row][col] * westView[row][col]
            max(newVal, innAcc)
        }, acc)
    }
}

fun parseInputFile() =
    File("src/main/kotlin/day08/input.txt")
        .readLines()
        .let{ lines ->
            Array(lines.size) {
                lines[it].asSequence().map { it.toString().toInt() }.toList().toTypedArray()
            }
        }

fun main() {
    println("The number of visible trees in the forest is ${countVisibleTrees()}")
    println("The tree with the maximum view field has a view field of ${calculateViewField()}")
}