val Char.priority: Int
    get() = when {
        isLowerCase() -> this - 'a' + 1
        else -> this - 'A' + 27
    }

data class Rucksack(val data: String) {
    val commonItem: Char
        get() {
            val (firstHalf, secondHalf) = data.splitHalf()
            return (firstHalf.toSet() intersect secondHalf.toSet()).first()
        }

    val itemSet
        get() = data.toSet()
}

data class RucksackGroup(val data: List<Rucksack>) {
    val badge: Char
        get() {
            var set = data[0].itemSet
            for (i in 1 until data.size) {
                set = set intersect data[i].itemSet
            }
            return set.first()
        }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { data -> Rucksack(data) }
            .sumOf { rucksack -> rucksack.commonItem.priority }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { data -> Rucksack(data) }
            .windowed(3, 3)
            .map { rucksacks -> RucksackGroup(rucksacks) }
            .sumOf { group -> group.badge.priority }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}