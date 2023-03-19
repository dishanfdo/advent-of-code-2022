import kotlin.math.abs

fun main() {
    data class Point(val x: Int, val y: Int) {
        fun manhattanDistance(to: Point): Int = abs(x - to.x) + abs(y - to.y)
    }

    data class Doublet(val sensor: Point, val beacon: Point) {
        fun coverageAtRow(row: Int): IntRange? {
            val beaconDistance = sensor.manhattanDistance(to = beacon)
            val sideLength = beaconDistance - abs(row - sensor.y)
            if (sideLength < 0) return null
            return (sensor.x - sideLength)..(sensor.x + sideLength)
        }
    }

    data class Field(val doublets: List<Doublet>, val maxSpan: Int = 4_000_000) {
        fun beaconsAtRow(row: Int): Int = doublets.distinctBy { it.beacon }.count { it.beacon.y == row }

        fun coverageAtRow(row: Int): Int {
            return doublets
                .mapNotNull { doublet -> doublet.coverageAtRow(row) }
                .let { coverages -> merge(coverages) }
                .sumOf { coverage -> coverage.count() }
        }

        fun possibleBeaconPositionAtRow(row: Int): Int? {
            val coverages = doublets
                .mapNotNull { doublet -> doublet.coverageAtRow(row) }
                .let { coverages -> merge(coverages) }

            if (coverages.size < 2) return null
            return coverages.first().last + 1
        }

        fun possibleBeaconPosition(): Point {
            for (y in 0 .. (maxSpan + 1)) {
                val x = possibleBeaconPositionAtRow(y)
                if (x != null) {
                    return Point(x, y)
                }
            }
            error("There should be exactly one location for beacon")
        }

        fun invalidBeaconPositions(row: Int) = coverageAtRow(row) - beaconsAtRow(row)
    }

    fun parseInput(input: List<String>): List<Doublet> {
        fun String.toPoint(): Point {
            val (x, y) = split(", ").map { it.substringAfter("=").toInt() }
            return Point(x, y)
        }

        return input.map { line ->
            val (sensorPosition, beaconPosition) = line
                .split(":")
                .map { it.substringAfter("at ").toPoint() }
            Doublet(sensorPosition, beaconPosition)
        }
    }

    fun part1(input: List<Doublet>, row: Int): Int {
        val field = Field(input)
        return field.invalidBeaconPositions(row)
    }

    fun part2(input: List<Doublet>): Long {
        val field = Field(input)
        val beacon = field.possibleBeaconPosition()
        return beacon.x * 4_000_000L + beacon.y
    }

    // test if implementation meets criteria from the description, like:
    check(part1(parseInput(readInput("Day15_test")), 10) == 26)
    check(part2(parseInput(readInput("Day15_test"))) == 56000011L)

    val input = parseInput(readInput("Day15"))
    part1(input, 2000000).println()
    part2(input).println()
}
