import java.util.Stack

fun main() {
    data class Move(val count: Int, val from: Int, val to: Int)

    data class Stacks(private val stacks: List<Stack<Char>>) {

        fun perform(move: Move, oneByOne: Boolean = true) {
            val fromStack = stacks[move.from]
            val toStack = stacks[move.to]
            if (oneByOne) {
                repeat(move.count) {
                    toStack.push(fromStack.pop())
                }
            } else {
                val tempStack = Stack<Char>()
                repeat(move.count) {
                    tempStack.push(fromStack.pop())
                }
                while (tempStack.isNotEmpty()) {
                    toStack.push(tempStack.pop())
                }
            }
        }

        val top: String
            get() {
                var s = ""
                for (stack in stacks) {
                    if (stack.isNotEmpty()) {
                        s += stack.peek()
                    }
                }
                return s
            }
    }

    fun parseInstructions(input: List<String>): List<Move> {
        fun parseEntry(entry: String): Move {
            val simplified = entry.replace(Regex("move | from | to "), ",")
            val (_, count, from, to) = simplified.split(",")
            return Move(count.toInt(), from.toInt() - 1, to.toInt() - 1)
        }

        val splitIndex = input.indexOfFirst { it == "" }
        return input.subList(splitIndex + 1, input.size).map { entry -> parseEntry(entry) }
    }

    fun parseStacks(input: List<String>): Stacks {
        val stackInputWithStackLabel = input.takeWhile { it != "" }
        val stackInput = stackInputWithStackLabel.subList(0, stackInputWithStackLabel.size - 1)
        val cratesByLevel = stackInput.map { entry -> entry.chunked(4).map { it[1] } }.reversed()
        val stackCount = cratesByLevel[0].size

        val data = List(stackCount) { Stack<Char>() }
        for (level in cratesByLevel) {
            for ((index, crate) in level.withIndex()) {
                if (crate != ' ') {
                    data[index].push(crate)
                }
            }
        }

        return Stacks(data)
    }

    fun parseInput(input: List<String>): Pair<Stacks, List<Move>> {
        val stacks = parseStacks(input)
        val moves = parseInstructions(input)
        return Pair(stacks, moves)
    }

    fun part1(input: List<String>): String {
        val (stacks, moves) = parseInput(input)
        for (move in moves) {
            stacks.perform(move)
        }
        return stacks.top
    }

    fun part2(input: List<String>): String {
        val (stacks, moves) = parseInput(input)
        for (move in moves) {
            stacks.perform(move, oneByOne = false)
        }
        return stacks.top
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}