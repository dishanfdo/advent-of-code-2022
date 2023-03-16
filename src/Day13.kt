import java.util.*

sealed class Packet : Comparable<Packet>

data class Simple(val value: Int) : Packet() {
    override fun compareTo(other: Packet): Int = when (other) {
        is Compound -> Compound(this) compareTo other
        is Simple -> value compareTo other.value
    }

    override fun toString(): String = "$value"
}

data class Compound(val values: MutableList<Packet> = mutableListOf()) : Packet() {
    fun add(value: Packet) {
        values.add(value)
    }

    override fun compareTo(other: Packet): Int = when (other) {
        is Compound -> {
            (values zip other.values)
                .firstOrNull { (self, other) -> self != other }
                ?.let { (self, other) -> self compareTo other }
                ?: (values.size - other.values.size)
        }

        is Simple -> this compareTo Compound(other)
    }

    override fun toString(): String = "$values"
}

fun Compound(vararg values: Packet): Compound {
    val collection = Compound()
    for (packet in values) {
        collection.add(packet)
    }
    return collection
}

typealias PacketPairList = List<Pair<Packet, Packet>>

fun main() {

    List(1) {}

    fun String.toPacket(): Packet {
        val stack = Stack<Compound>()
        val token = mutableListOf<Char>()
        var current: Packet? = null

        fun addTokenToCompound() {
            val value = token.joinToString("").toInt()
            token.clear()
            stack.peek().add(Simple(value))
        }

        for (c in this) {
            if (c == '[') {
                val compound = Compound()
                if (stack.isNotEmpty()) {
                    stack.peek().add(compound)
                }
                stack.push(compound)
            } else if (c == ']') {
                if (token.isNotEmpty()) {
                    addTokenToCompound()
                }
                current = stack.pop()
            } else if (c.isDigit()) {
                token.add(c)
            } else if (c == ',') {
                if (token.isNotEmpty()) {
                    addTokenToCompound()
                }
            }
        }
        return current!!
    }

    fun parseInput(input: List<String>): PacketPairList = input
        .filter { it.isNotEmpty() }
        .chunked(2)
        .map { data ->
            val (left, right) = data
            left.toPacket() to right.toPacket()
        }

    fun part1(input: PacketPairList): Int {
        return input
            .withIndex()
            .filter { (_, pair) ->
                pair.first < pair.second
            }
            .sumOf { (i, _) -> i + 1 }
    }

    fun part2(input: PacketPairList): Int {
        val packets: List<Packet> = input.flatMap { pair -> listOf(pair.first, pair.second) }
        val divider1 = Compound(Compound(Simple(2)))
        val divider2 = Compound(Compound(Simple(6)))
        val packetsWithDividers = listOf(divider1, divider2, *packets.toTypedArray()).sorted()
        val index1 = packetsWithDividers.indexOf(divider1) + 1
        val index2 = packetsWithDividers.indexOf(divider2) + 1
        return index1 * index2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = parseInput(readInput("Day13_test"))
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = parseInput(readInput("Day13"))
    //input.forEach { it.println() }
    part1(input).println()
    part2(input).println()
}
