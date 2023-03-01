import java.lang.Integer.max

fun main() {
    data class Grid(val data: List<List<Int>>) {
        val length = data.size
        val width = data[0].size

        operator fun get(x: Int, y: Int): Int {
            return data[y][x]
        }

        val visibleTreeCount: Int
            get() {
                var count = 0
                for (x in 0 until width) {
                    for (y in 0 until length) {
                        if (isVisible(x, y)) {
                            count++
                        }
                    }
                }
                return count
            }

        fun getTreesToRight(x: Int, y: Int) = data[y].subList(fromIndex = x + 1, toIndex = width)

        fun getTreesToLeft(x: Int, y: Int) = data[y].subList(fromIndex = 0, toIndex = x).reversed()

        fun getTreesToFront(x: Int, y: Int) = data.map { it[x] }.subList(fromIndex = 0, toIndex = y).reversed()

        fun getTreesToBack(x: Int, y: Int) = data.map { it[x] }.subList(fromIndex = y + 1, toIndex = length)

        fun isVisible(x: Int, y: Int): Boolean {
            val height = this[x, y]
            return getTreesToRight(x, y).all { it < height } ||
                    getTreesToLeft(x, y).all { it < height } ||
                    getTreesToFront(x, y).all { it < height } ||
                    getTreesToBack(x, y).all { it < height }
        }

        fun scenicScore(x: Int, y: Int): Int {
            val height = this[x, y]
            val leftScore = scenicScore(height, getTreesToLeft(x, y))
            val rightScore = scenicScore(height, getTreesToRight(x, y))
            val frontScore = scenicScore(height, getTreesToFront(x, y))
            val backScore = scenicScore(height, getTreesToBack(x, y))
            return leftScore * rightScore * frontScore * backScore
        }

        private fun scenicScore(height: Int, trees: List<Int>): Int {
            val score = trees.indexOfFirst { it >= height }
            return if (score == -1) trees.size else score + 1
        }
    }

    fun parseInput(input: List<String>): Grid {
        val data = input.map { line -> line.map { it.digitToInt() } }
        return Grid(data)
    }

    fun part1(grid: Grid): Int {
        return grid.visibleTreeCount
    }

    fun part2(grid: Grid): Int {
        var maxScenicScore = 0
        for (x in 0 until grid.width) {
            for (y in 0 until grid.length) {
                maxScenicScore = max(maxScenicScore, grid.scenicScore(x, y))
            }
        }
        return maxScenicScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(parseInput(testInput)) == 21)
    check(part2(parseInput(testInput)) == 8)

    val input = readInput("Day08")
    val grid = parseInput(input)
    part1(grid).println()
    part2(grid).println()
}
