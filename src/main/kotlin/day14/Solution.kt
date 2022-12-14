import java.io.File

class Obstacle(
    rawStart: String,
    rawEnd: String
) {
    val left: Int
    private val top: Int
    val right: Int
    val bottom: Int

    init {
        val tmpLeft = rawStart.split(',')[0].toInt()
        val tmpRight = rawEnd.split(',')[0].toInt()
        left = minOf(tmpLeft, tmpRight)
        right = maxOf(tmpLeft, tmpRight)

        val tmpTop = rawStart.split(',')[1].toInt()
        val tmpBottom = rawEnd.split(',')[1].toInt()
        top = minOf(tmpTop, tmpBottom)
        bottom = maxOf(tmpTop, tmpBottom)
    }

    fun collides(x: Int, y: Int) =
        (x in left..right) && (y in top .. bottom)
}

class Grid(
    obstacles: List<Obstacle>
) {
    private val bottomLine = obstacles.fold(0) { acc, it -> if(it.bottom > acc) it.bottom else acc }
    private var offset = obstacles.fold(Int.MAX_VALUE) { acc, it -> if(it.left < acc) it.left else acc }
    private val rightmostLine = obstacles.fold(0) { acc, it -> if(it.right > acc) it.right else acc }
    private var grain = Pair(500, 0)
    private var grainCount = 1
    private val grid: List<MutableList<Char>> = List(bottomLine+3) { y ->
        MutableList(rightmostLine - offset + 1) { x ->
            '#'.takeIf { obstacles.any{ it.collides(x+offset, y)} } ?: '.'
        }
    }.apply {
        this[0].indices.onEach {
            this[bottomLine+2][it] = '#'
        }
    }

    override fun toString() = grid.joinToString(separator="") { line ->
        line.joinToString(separator = "") { it.toString() } + "\n"
    }

    private fun incrementLeft() {
        (0 until bottomLine+2).onEach {
            grid[it].add(0, '.')
        }
        grid[bottomLine+2].add(0, '#')
        offset -= 1
    }

    private fun incrementRight() {
        (0 until bottomLine+2).onEach {
            grid[it].add('.')
        }
        grid[bottomLine+2].add('#')
    }

    private fun step() {
        grid[grain.second][grain.first-offset] = 'o'
        if(grain.first-offset == 0) incrementLeft()
        if(grain.first-offset+1 == grid[grain.second].size) incrementRight()
        //println(this.toString())
        val next =
            if(grid[grain.second+1][grain.first-offset] == '.') Pair(grain.first, grain.second+1)
            else if (grid[grain.second+1][grain.first-offset-1] == '.') Pair(grain.first-1, grain.second+1)
            else if (grid[grain.second+1][grain.first-offset+1] == '.') Pair(grain.first+1, grain.second+1)
            else Pair(500, 0)

        if (next != Pair(500, 0)){
            grid[grain.second][grain.first-offset] = '.'
        } else {
            grainCount += 1
        }
        grain = next
    }

    fun render2(): Int {
        while(grid[1][499-offset] == '.' ||
            grid[1][500-offset] == '.' ||
            grid[1][501-offset] == '.') {
            step()
        }
        return grainCount
    }

    fun render(): Int {
        while(grain.second < bottomLine) {
            step()
        }
        return grainCount-1
    }
}

fun parseInputFile() =
    File("src/main/kotlin/day14/input.txt")
        .readLines()
        .fold(emptyList<Obstacle>()) { acc, line ->
            val points = line.split(" -> ")
            acc + (0 until points.size-1).fold(emptyList()) { it, idx ->
                it + Obstacle(points[idx], points[idx+1])
            }
        }


fun main() {
    val obstacles = parseInputFile()
    val grid = Grid(obstacles)
    val count = grid.render2()
    println("The number of stable grains is: $count")
}