import java.util.LinkedList

fun main() {
    data class Point(val x: Int, val y: Int)

    data class Heightmap(val map: Grid<Int>, val start: Point, val end: Point) {

        fun isReachable(from: Point, to: Point): Boolean {
            val a = map[from.x, from.y]
            val b = map[to.x, to.y]
            return a + 1 >= b
        }

        fun isReachableInReverse(from: Point, to: Point): Boolean = isReachable(to, from)

        fun optimalStepsFromGivenStart(): Int = optimalPathSteps(
            from = start,
            isEnd = { it == end },
            canMove = ::isReachable,
        )

        /**
         * Find the path from end (i.e. in reverse) until we reach any possible starting point
         */
        fun optimalStepsFromAnyPossibleStart(): Int = optimalPathSteps(
            from = end,
            isEnd = { map[it.x, it.y] == 0 },
            canMove = ::isReachableInReverse
        )

        fun optimalPathSteps(
            from: Point,
            isEnd: (Point) -> Boolean,
            canMove: (from: Point, to: Point) -> Boolean,
        ): Int {
            val visited = HashSet<Point>()
            fun isVisited(point: Point): Boolean = point in visited

            val queue = LinkedList<Pair<Int, Point>>()
            queue.offer(0 to from)
            visited.add(from)
            while (queue.isNotEmpty()) {
                val (steps, current) = queue.poll()
                if (isEnd(current)) {
                    return steps
                }

                val directions = listOf(Point(0, 1), Point(0, -1), Point(1, 0), Point(-1, 0))
                for (direction in directions) {
                    val (dx, dy) = direction
                    val nextPoint = Point(current.x + dx, current.y + dy)
                    if (
                        map.isValidIndex(nextPoint.x, nextPoint.y) &&
                        !isVisited(nextPoint) &&
                        canMove(current, nextPoint)
                    ) {
                        queue.add((steps + 1) to nextPoint)
                        visited.add(nextPoint)
                    }
                }
            }

            return Int.MAX_VALUE
        }
    }

    fun Char.toHeight(): Int = when (this) {
        'S' -> 0
        'E' -> 25
        else -> this - 'a'
    }

    fun parseInput(input: List<String>): Heightmap {
        val rows = input.size
        val cols = input[0].length
        val map = Grid(rows, cols, 0)
        var start = Point(0, 0)
        var end = Point(0, 0)
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val char = input[row][col]
                map[col, row] = char.toHeight()
                if (char == 'S') {
                    start = Point(col, row)
                }
                if (char == 'E') {
                    end = Point(col, row)
                }
            }
        }

        return Heightmap(map, start, end)
    }

    fun part1(heightmap: Heightmap): Int {
        return heightmap.optimalStepsFromGivenStart()
    }

    fun part2(heightmap: Heightmap): Int {
        return heightmap.optimalStepsFromAnyPossibleStart()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val parsed = parseInput(testInput)
    check(part1(parsed) == 31)
    check(part2(parsed) == 29)

    part1(parseInput(readInput("Day12"))).println()
    part2(parseInput(readInput("Day12"))).println()
}
