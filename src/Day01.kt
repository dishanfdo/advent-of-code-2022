fun main() {
    fun processInput(input: String): List<List<Int>> {
        return input
            .split("\n\n")
            .map { elf -> elf.lines().map { it.toInt() } }
    }

    fun part1(input: String): Int {
        val calories = processInput(input)
        return calories.maxOf { it.sum() }
    }

    fun part2(input: String): Int {
        val calories = processInput(input)
        return calories.map { it.sum() }.sorted().takeLast(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputText("Day01_test")
    check(part1(testInput) == 30)
    check(part2(testInput) == 75)

    val input = readInputText("Day01")
    part1(input).println()
    part2(input).println()
}
