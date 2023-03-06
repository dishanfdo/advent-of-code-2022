sealed class Instruction(val length: Int) {
    abstract fun execute(cpu: CPU): Boolean
}
object NoOp: Instruction(length = 1) {
    override fun execute(cpu: CPU): Boolean {
        return true
    }

    override fun toString(): String = "NoOp"
}

data class AddX(val v: Int): Instruction(length = 2) {
    private var progress = 0
    override fun execute(cpu: CPU): Boolean {
        progress++
        if (progress == length) {
            cpu.x += v
        }
        return (progress >= length)
    }
}

class CPU(var x: Int = 1) {
    var instructions: List<Instruction> = mutableListOf()
    private var ip = 0
    var clock: Int = 0

    fun clockTick(): Pair<Boolean, Int> {
        clock++
        val oldX = x
        val done = instructions[ip].execute(this)
        if (done) {
            ip++
        }
        return Pair(ip < instructions.size, clock * oldX)
    }
}

class CRT(val width: Int, val height: Int) {
    val screen = Grid(rows = height, cols = width, '.')
    var clock = 0

    fun show() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                print(screen[x, y])
            }
            kotlin.io.println()
        }
    }

    fun clockTick(spriteCenter: Int) {
        val x = clock % 40
        val y = clock / 40
        val pixelValue = if (x in spriteCenter -1 .. spriteCenter + 1 ) '#' else '.'
        screen[x, y] = pixelValue
        clock++
    }
}

class Device() {
    val cpu = CPU()
    val crt = CRT(width = 40, height = 6)
    var instructions: List<Instruction>
        set(value) { cpu.instructions = value }
        get() = cpu.instructions

    fun show() {
        crt.show()
    }

    fun start() {
        do {
            crt.clockTick(spriteCenter = cpu.x)
            val (processing, _) = cpu.clockTick()
        } while (processing)
    }
}

fun main() {

    fun parseInput(input: List<String>): List<Instruction> {
        fun parseInstruction(s: String): Instruction {
            return when (s.substring(0..3)) {
                "noop" -> NoOp
                "addx" -> {
                    val (_, value) = s.split(" ")
                    AddX(value.toInt())
                }
                else -> error("Invalid instruction: $s")
            }
        }

        return input.map { parseInstruction(it) }
    }

    fun part1(instructions: List<Instruction>): Int {
        val cpu = CPU()
        cpu.instructions = instructions
        var c = 0
        do {
            val (processing, strength) = cpu.clockTick()
            val clock = cpu.clock
            if ((clock - 20) % 40 == 0) {
                c += strength
            }
        } while (processing)
        return c
    }

    fun part2(instructions: List<Instruction>) {
        val device = Device()
        device.instructions = instructions
        device.start()
        device.show()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(parseInput(testInput)) == 13140)

    val input = readInput("Day10")
    part1(parseInput(input)).println()
    part2(parseInput(input))
}
