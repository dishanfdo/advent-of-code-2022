fun main() {
    fun String.toIntRange(): IntRange {
        val (start, end) = split("-").map { it.toInt() }
        return start .. end
    }

    fun parse(entry: String): Pair<IntRange, IntRange> {
        val (a, b) = entry.split(",")
        return Pair(a.toIntRange(), b.toIntRange())
    }

    fun Pair<IntRange, IntRange>.hasFullOverlap() = hasFullOverlap(this.first, this.second)

    fun Pair<IntRange, IntRange>.hasPartialOverlap() = hasOverlap(this.first, this.second)

    fun part1(input: List<String>): Int {
        return input
            .map { entry -> parse(entry) }
            .count { pair -> pair.hasFullOverlap() }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { entry -> parse(entry) }
            .count { pair -> pair.hasPartialOverlap() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
