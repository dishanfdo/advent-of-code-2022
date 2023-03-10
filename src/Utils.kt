import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String): List<String> = File("src", "$name.txt")
    .readLines()

fun readInputText(name: String): String = File("src", "$name.txt")
    .readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun String.splitHalf(): Pair<String, String> {
    val mid = length / 2
    return Pair(substring(0, mid), substring(mid))
}

fun IntRange.fullyContain(other: IntRange): Boolean = start <= other.first && other.last <= endInclusive

fun hasFullOverlap(a: IntRange, b: IntRange): Boolean = a.fullyContain(b) || b.fullyContain(a)

fun hasOverlap(a: IntRange, b: IntRange): Boolean = a.contains(b.first) || b.contains(a.first)

data class Grid<T>(val rows: Int, val cols: Int, val initial: T) {
    val data = MutableList(rows) { MutableList(cols) { initial } }

    operator fun get(x: Int, y: Int): T {
        return data[y][x]
    }

    operator fun set(x: Int, y: Int, value: T) {
        data[y][x] = value
    }

    fun isValidIndex(x: Int, y: Int): Boolean = x in 0 until cols && y in 0 until rows
}