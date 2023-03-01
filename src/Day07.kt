import java.util.*

sealed class InputEntry
object MoveUp : InputEntry()
data class MoveDownTo(val dir: String) : InputEntry()
data class FileEntry(val size: Int, val name: String) : InputEntry()
object Ignored: InputEntry()

sealed class Item {
    abstract val size: Int
    abstract val name: String
}

data class Directory(
    override val name: String,
    val children: MutableList<Item> = mutableListOf(),
) : Item() {
    override val size: Int
        get() = children.sumOf { it.size }

    fun addItem(item: Item) {
        children.add(item)
    }

    fun listDirs(recursive: Boolean = false): List<Directory> {
        val dirs = mutableListOf<Directory>()
        children.filterIsInstance<Directory>().forEach { dir ->
            dirs.add(dir)
            if (recursive) {
                dirs.addAll(dir.listDirs(recursive = true))
            }
        }
        return dirs
    }
}

data class File(override val name: String, override val size: Int) : Item()

fun main() {
    fun parseInput(input: List<String>): List<InputEntry> {
        fun parseChangeDirectoryCommand(command: String): InputEntry {
            return when (val dir = command.split(" ")[2]) {
                "/" -> Ignored
                ".." -> MoveUp
                else -> MoveDownTo(dir)
            }
        }

        fun parseListEntry(entry: String): InputEntry {
            val (p1, p2) = entry.split(" ")
            return when (p1) {
                "dir" -> Ignored
                else -> FileEntry(size = p1.toInt(), name = p2)
            }
        }

        fun parseLine(input: String): InputEntry {
            return when (input.substring(0..3)) {
                "$ cd" -> parseChangeDirectoryCommand(input)
                "$ ls" -> Ignored
                else -> parseListEntry(input)
            }
        }

        return input.map { line -> parseLine(line) }
    }

    fun noOperation() {}

    fun createDirectoryStructure(input: List<InputEntry>): Directory {
        val stack: Stack<Directory> = Stack()
        val rootDir = Directory("/")
        stack.push(rootDir)

        for (entry in input) {
            when (entry) {
                is MoveDownTo -> {
                    val currentDir = stack.peek()
                    val newDir = Directory(entry.dir)
                    currentDir.addItem(newDir)
                    stack.push(newDir)
                }

                MoveUp -> stack.pop()
                is FileEntry -> {
                    val currentDir = stack.peek()
                    val newFile = File(name = entry.name, size = entry.size)
                    currentDir.addItem(newFile)
                }

                else -> noOperation()
            }
        }

        return rootDir
    }

    fun part1(rootDir: Directory): Int {
        return rootDir
            .listDirs(recursive = true)
            .filter { it.size < 100000 }
            .sumOf { it.size }
    }

    fun part2(rootDir: Directory): Int {
        val totalUsedSpace = rootDir.size
        val freeSpace = 70_000_000 - totalUsedSpace
        val additionalSpaceRequired = 30_000_000 - freeSpace

        return rootDir
            .listDirs(recursive = true)
            .sortedBy { it.size }
            .first { it.size > additionalSpaceRequired }
            .size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    val testRootDir = createDirectoryStructure(parseInput(testInput))
    check(part1(testRootDir) == 95437)

    val input = readInput("Day07")
    val parsedInput = parseInput(input)
    val rootDir = createDirectoryStructure(parsedInput)
    part1(rootDir).println()
    part2(rootDir).println()
}
