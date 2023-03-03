fun main() {
    data class Point(val x: Int, val y: Int)

    operator fun Point.plus(other: Point): Point {
        return Point(this.x + other.x, this.y + other.y)
    }

    operator fun Point.minus(other: Point): Point {
        return Point(this.x - other.x, this.y - other.y)
    }

    data class Knot(
        var position: Point = Point(0, 0),
    ) {
        fun moveBy(ds: Point) {
            position += ds
        }

        private fun Point.isTouching(p: Point): Boolean {
            val dx = x - p.x
            val dy = y - p.y
            return dx in -1..1 && dy in -1..1
        }

        fun follow(other: Knot): Boolean {
            val otherPosition = other.position
            if (otherPosition.isTouching(position)) return false
            val dx = otherPosition.x - position.x
            val dy = otherPosition.y - position.y

            fun normalize(t: Double): Int = when (t) {
                -0.5 -> -1
                0.5 -> 1
                else -> t.toInt()
            }

            val targetX = normalize(dx / 2.0)
            val targetY = normalize(dy / 2.0)
            position += Point(targetX, targetY)
            return true
        }
    }

    class Field(knotCount: Int) {
        val knots = List(knotCount) { Knot() }
        val head = knots.first()
        val tail = knots.last()

        val tailTrack = mutableSetOf<Point>()

        fun moveHead(command: String) {
            val displacementSequence = displacementSequenceFor(command)
            for (ds in displacementSequence) {
                updateRope(ds)
                tailTrack.add(tail.position)
            }
        }

        private fun updateRope(displacement: Point) {
            head.moveBy(displacement)
            for (i in 1 until knots.size) {
                val headKnot = knots[i - 1]
                val tailKnot = knots[i]
                val followed = tailKnot.follow(headKnot)
                if (!followed) break
            }
        }

        private fun displacementSequenceFor(command: String): List<Point> {
            val (direction, stepsStr) = command.split(" ")
            val displacementUnit = when (direction) {
                "R" -> Point(1, 0)
                "L" -> Point(-1, 0)
                "U" -> Point(0, 1)
                "D" -> Point(0, -1)
                else -> error("Invalid direction: $direction")
            }
            val steps = stepsStr.toInt()
            return (1..steps).map { displacementUnit }
        }
    }

    fun part1(input: List<String>): Int {
        val field = Field(knotCount = 2)
        for (command in input) {
            field.moveHead(command)
        }
        return field.tailTrack.size
    }

    fun part2(input: List<String>): Int {
        val field = Field(knotCount = 10)
        for (command in input) {
            field.moveHead(command)
        }
        return field.tailTrack.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
