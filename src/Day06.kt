fun main() {
    fun String.findMarkerStart(size: Int): Int {
        if (length < size) {
            throw IllegalArgumentException("Invalid data")
        }
        for (i in (size - 1) until length) {
            val count = substring((i - (size - 1))..i).toSet().size
            if (count == size) {
                return i + 1
            }
        }
        throw IllegalArgumentException("Invalid data")
    }

    fun part1(input: List<String>): Int {
        val data = input.first()
        return data.findMarkerStart(4)
    }

    fun part2(input: List<String>): Int {
        val data = input.first()
        return data.findMarkerStart(14)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 11)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
