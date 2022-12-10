package day10

import java.io.File

data class RegisterHistory(
    val history: Map<Int, Int> = mapOf(0 to 1)
) {
    fun addNoOp(): RegisterHistory {
        val lastClock = history.keys.last()
        val lastRegister = history.values.last()
        return this.copy(history = history + (lastClock+1 to lastRegister))
    }

    fun addAddxOp(amount: Int): RegisterHistory {
        val lastClock = history.keys.last()
        val lastRegister = history.values.last()
        return this.copy(history = history + (lastClock + 2 to lastRegister + amount))
    }

    fun getRegisterValueAtClock(clock: Int) =
        history.keys.last { it < clock }.let {
            history[it]
        } ?: 0
}

data class CrtRender(
    val crt: List<Char> = emptyList()
) {

    fun renderSprite(spriteCenter: Int): CrtRender =
        if (crt.size < 240) {
            val adjustedSprite = spriteCenter + (crt.size/40) * 40
            if (crt.size >= adjustedSprite-1 && crt.size <= adjustedSprite+1)
                this.copy(crt = crt + listOf('#'))
            else this.copy(crt = crt + listOf('.'))
        } else this

    fun printRendered() {
        crt.chunked(40).onEach {
            println(String(it.toCharArray()))
        }
    }

}


fun parseInputFile() =
    File("src/main/kotlin/day10/input.txt")
        .readLines()
        .fold(RegisterHistory()) { acc, cmd ->
            if(cmd == "noop") acc.addNoOp()
            else {
                val (_, amount) = cmd.split(" ")
                acc.addAddxOp(amount.toInt())
            }
        }

fun findSignalLevelAtClocks() = parseInputFile()
    .let { registerHistory ->
        List(6){ it*40+20 }.fold(0) { sum, it ->
            sum + registerHistory.getRegisterValueAtClock(it)*it
        }
    }

fun renderCrt() = parseInputFile()
    .let {
        (1 .. 240).fold(CrtRender()) { crt, clock ->
            crt.renderSprite(it.getRegisterValueAtClock(clock))
        }.printRendered()
    }

fun main() {
    println("The sum of the signal strengths during the 20th, 60th, 100th, 140th, 180th, and 220th cycles is: ${findSignalLevelAtClocks()}")
    println("The final CRT render is:")
    renderCrt()
}