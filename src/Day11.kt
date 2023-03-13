fun main() {
    data class Monkey(
        val items: MutableList<Long> = mutableListOf(),
        val operation: (Long) -> Long,
        val next: (Long) -> Int,
        val divisor: Long,
    ) {
        var inspections = 0

        fun takeTurn(gang: List<Monkey>, worryLevelModifier: (Long) -> Long) {
            while (items.isNotEmpty()) {
                val item = items.removeFirst()
                val inspected = worryLevelModifier(inspect(item))
                val passTo = next(inspected)
                pass(inspected, to = gang[passTo])
                inspections++
            }
        }

        fun inspect(item: Long): Long = operation(item)

        fun pass(item: Long, to: Monkey) {
            to.items.add(item)
        }

        override fun toString() = "Monkey(items=$items, ins: $inspections)"
    }

    fun parseOperation(input: String): (Long) -> Long {
        val operation = input.removePrefix("  Operation: new = old ")
        val (operand, value) = operation.split(" ")
        return when (operand) {
            "+" -> { x: Long -> x + value.toLong() }
            "*" -> when (value) {
                "old" -> { x: Long -> x * x }
                else -> { x: Long -> x * value.toLong() }
            }

            else -> error("Invalid operation: $input")
        }
    }

    fun parseTest(input: List<String>): (Long) -> Int {
        val divisor = input[3].removePrefix("  Test: divisible by ").toLong()
        val valueIfTrue = input[4].removePrefix("    If true: throw to monkey ").toInt()
        val valueIfFalse = input[5].removePrefix("    If false: throw to monkey ").toInt()

        return { x: Long ->
            if (x % divisor == 0L) valueIfTrue else valueIfFalse
        }
    }

    fun parseMonkey(data: List<String>): Monkey {
        val (_, itemsData) = data[1].split(": ")
        val items = itemsData.split(", ").map { it.toLong() }.toMutableList()
        val operation = parseOperation(data[2])
        val test = parseTest(data)
        val divisor = data[3].removePrefix("  Test: divisible by ").toLong()
        return Monkey(items, operation, test, divisor)
    }

    fun parseInput(input: List<String>): List<Monkey> = input
        .filter { it.isNotEmpty() }
        .chunked(6)
        .map { monkeyData -> parseMonkey(monkeyData) }

    fun levelOfMonkeyBusiness(monkeys: List<Monkey>): Long = monkeys
        .sortedByDescending { monkey -> monkey.inspections }
        .take(2)
        .map { it.inspections.toLong() }.reduce { a, b -> a * b }


    fun part1(monkeys: List<Monkey>): Long {
        for (round in 1..20) {
            for (monkey in monkeys) {
                monkey.takeTurn(monkeys) { wl -> wl / 3 }
            }
        }

        return levelOfMonkeyBusiness(monkeys)
    }

    fun part2(monkeys: List<Monkey>): Long {
        val worryFactor = monkeys.map { it.divisor }.reduce { a, b -> a * b }
        for (round in 1..10_000) {
            for (monkey in monkeys) {
                monkey.takeTurn(monkeys) { wl -> wl % worryFactor }
            }
        }

        return levelOfMonkeyBusiness(monkeys)
    }

    // test if implementation meets criteria from the description, like:
    check(part1(parseInput(readInput("Day11_test"))) == 10605L)
    check(part2(parseInput(readInput("Day11_test"))) == 2713310158L)

    part1(parseInput(readInput("Day11"))).println()
    part2(parseInput(readInput("Day11"))).println()
}
