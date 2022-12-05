import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day5.txt").toFile().readText()

    val lines = input.split("\n").filter { it.isNotEmpty() }
    val stackInput = lines.take(8).map { it.asStackInput() }
    val moveInput = lines.subList(9, lines.lastIndex + 1).map { it.asMoveInput() }

    val stacks = (0..8).map { Stack(stackInput, it) }
    val stacksCopy = (0..8).map { Stack(stackInput, it) }

    moveInput.forEach { stacks[it.sourceStack].moveTo(stacks[it.destStack], it.count) }

    print("Results part one: ")
    println(stacks.joinToString("") { it.topElement.toString() })

    moveInput.forEach { stacksCopy[it.sourceStack].moveToMultiple(stacksCopy[it.destStack], it.count) }

    print("Results part two: ")
    println(stacksCopy.joinToString("") { it.topElement.toString() })
}

private fun String.asStackInput(): List<Char> {
    val itemsInStack = mutableListOf<Char>()

    for (i in 1 until length step 4) {
        itemsInStack.add(this[i])
    }

    return itemsInStack
}

private fun String.asMoveInput(): Move {
    val match = Regex("move (\\d+) from (\\d+) to (\\d+)").find(this)!!
    val (count, source, dest) = match.destructured

    return Move(count.toInt(), source.toInt() - 1, dest.toInt() - 1)
}

data class Move(val count: Int, val sourceStack: Int, val destStack: Int)

class Stack(input: List<List<Char>>, stackIdx: Int) {
    private val contents: ArrayDeque<Char>

    val topElement: Char get() = contents[0]

    init {
        contents = ArrayDeque(input.map { it[stackIdx] }.filter { it != ' ' })
    }

    fun moveTo(other: Stack, count: Int) {
        for (i in 0 until count) {
            other.contents.addFirst(contents.removeFirst())
        }
    }

    fun moveToMultiple(other: Stack, count: Int) {
        other.contents.addAll(0, contents.take(count))
        (0 until count).forEach { _ -> contents.removeFirst() }
    }
}