import Outcome.*
import Shape.*
import kotlin.IllegalArgumentException

enum class Shape {
    Rock, Paper, Scissors;

    val score: Int
        get() = when (this) {
            Rock -> 1
            Paper -> 2
            Scissors -> 3
        }
}

enum class Outcome {
    Lose, Draw, Win;

    val score: Int
        get() = when (this) {
            Lose -> 0
            Draw -> 3
            Win -> 6
        }
}

fun main() {

    fun processInput(input: List<String>): List<Pair<String, String>> {
        return input.map {
            val (a, b) = it.split(" ")
            a to b
        }
    }

    fun String.toShape() = when (this) {
        "A", "X" -> Rock
        "B", "Y" -> Paper
        "C", "Z" -> Scissors
        else -> throw IllegalArgumentException("Invalid shape: $this")
    }

    fun String.toOutcome() = when (this) {
        "X" -> Lose
        "Y" -> Draw
        "Z" -> Win
        else -> throw IllegalArgumentException("Invalid outcome: $this")
    }

    fun outcome(opponentShape: Shape, myShape: Shape): Outcome = when (opponentShape to myShape) {
        Rock to Paper, Paper to Scissors, Scissors to Rock -> Win
        Rock to Rock, Paper to Paper, Scissors to Scissors -> Draw
        else -> Lose
    }

    fun shapeFor(opponentShape: Shape, outcome: Outcome): Shape = when (opponentShape to outcome) {
        Rock to Win, Paper to Draw, Scissors to Lose -> Paper
        Rock to Draw, Paper to Lose, Scissors to Win -> Rock
        else -> Scissors
    }

    fun part1(input: List<String>): Int {
        return processInput(input).sumOf { data ->
            val opponentShape = data.first.toShape()
            val myShape = data.second.toShape()
            val outcome = outcome(opponentShape, myShape)
            return@sumOf myShape.score + outcome.score
        }
    }

    fun part2(input: List<String>): Int {
        return processInput(input).sumOf { data ->
            val opponentShape = data.first.toShape()
            val outcome = data.second.toOutcome()
            val myShape = shapeFor(opponentShape, outcome)
            return@sumOf myShape.score + outcome.score
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
