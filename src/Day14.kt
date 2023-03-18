import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    data class Point(val x: Int, val y: Int) {
        override fun toString(): String = "($x, $y)"
    }

    data class Rect(val topLeft: Point, val bottomRight: Point) {
        operator fun contains(p: Point): Boolean {
            val inX = p.x in topLeft.x..bottomRight.x
            val inY = p.y in topLeft.y..bottomRight.y
            return inX && inY
        }
    }

    data class Cave(val origin: Point) {
        private val rocks = mutableSetOf<Point>()
        private val sand = mutableSetOf<Point>()
        private var bottom: Int? = null

        val sandCount: Int
            get() = sand.size

        val bounds: Rect
            get() {
                val left = if (bottom != null) Int.MIN_VALUE else rocks.minOf { it.x }
                val right = if (bottom != null) Int.MAX_VALUE else rocks.maxOf { it.x }
                val top = 0
                val bottom = bottom ?: rocks.maxOf { it.y }

                return Rect(Point(left, top), Point(right, bottom))
            }

        val viewPort: Rect
            get() {
                val allPoints = rocks union sand
                val left = allPoints.minOf { it.x }
                val right = allPoints.maxOf { it.x }
                val top = 0
                val bottom = bottom ?: allPoints.maxOf { it.y }

                return Rect(Point(left, top), Point(right, bottom))
            }

        fun Point.isTouchingBottom(): Boolean = this.y == bottom
        fun Point.isBlocked() = this in rocks || this in sand || this.isTouchingBottom()
        fun Point.isNotBlocked() = !this.isBlocked()

        fun addBottom(levelsBelow: Int = 2) {
            bottom = rocks.maxOf { it.y } + levelsBelow
        }

        fun addRockPath(path: List<Point>) {
            path.zipWithNext().forEach { (p1, p2) ->
                val dx = abs(p2.x - p1.x)
                if (dx > 0) {
                    val start = min(p1.x, p2.x)
                    val end = max(p1.x, p2.x)
                    for (x in start..end) {
                        rocks.add(Point(x, p1.y))
                    }
                }

                val dy = abs(p2.y - p1.y)
                if (dy > 0) {
                    val start = min(p1.y, p2.y)
                    val end = max(p1.y, p2.y)
                    for (y in start..end) {
                        rocks.add(Point(p1.x, y))
                    }
                }
            }
        }

        fun addRockPaths(paths: List<List<Point>>) {
            for (path in paths) {
                addRockPath(path)
            }
        }

        private fun findNextRestingPoint(current: Point): Point? {
            val down = Point(current.x, current.y + 1)
            val downLeft = Point(current.x - 1, current.y + 1)
            val downRight = Point(current.x + 1, current.y + 1)

            val firstUnblocked = listOf(down, downLeft, downRight).firstOrNull { it.isNotBlocked() }
            val next = firstUnblocked ?: return current
            if (next !in bounds) {
                return null
            }
            return findNextRestingPoint(next)
        }

        fun isFilledWithSand() = origin.isBlocked() || findNextRestingPoint(current = origin) == null

        fun addSandUnit(): Boolean {
            if (origin.isBlocked()) return false
            val restPoint = findNextRestingPoint(current = origin) ?: return false
            sand.add(restPoint)
            return true
        }

        fun print() {
            val bounds = viewPort
            val xRange = bounds.topLeft.x - 1..bounds.bottomRight.x + 1
            val yRange = 0..bounds.bottomRight.y
            for (y in yRange) {
                for (x in xRange) {
                    val point = Point(x, y)
                    val pix = when {
                        point in rocks || point.isTouchingBottom() -> "#"
                        point in sand -> "o"
                        else -> "."
                    }
                    print(pix)
                }
                kotlin.io.println()
            }
        }
    }

    fun parseInput(input: List<String>): List<List<Point>> {
        fun String.toPoint(): Point {
            val (x, y) = split(",").map { it.toInt() }
            return Point(x, y)
        }

        fun String.toRockPath(): List<Point> = split(" -> ").map { it.toPoint() }

        return input.map { it.toRockPath() }
    }

    fun part1(paths: List<List<Point>>, printCave: Boolean = false): Int {
        val cave = Cave(Point(500, 0))
        cave.addRockPaths(paths)

        while (!cave.isFilledWithSand()) {
            cave.addSandUnit()
        }

        if (printCave) {
            println()
            cave.print()
        }

        return cave.sandCount
    }

    fun part2(paths: List<List<Point>>, printCave: Boolean = false): Int {
        val cave = Cave(Point(500, 0))
        cave.addRockPaths(paths)
        cave.addBottom()

        while (!cave.isFilledWithSand()) {
            cave.addSandUnit()
        }

        if (printCave) {
            println()
            cave.print()
        }

        return cave.sandCount
    }

    // test if implementation meets criteria from the description, like:
    val testInput = parseInput(readInput("Day14_test"))
    check(part1(testInput, printCave = true) == 24)
    check(part2(testInput, printCave = true) == 93)

    val input = parseInput(readInput("Day14"))
    part1(input).println()
    part2(input).println()
}
