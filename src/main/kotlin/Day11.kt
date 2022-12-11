import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day11.txt").toFile().readText()

    val monkeys1 = input.split("\n\n").filter { it.isNotEmpty() }.map { it.toMonkey() }
    val monkeys2 = input.split("\n\n").filter { it.isNotEmpty() }.map { it.toMonkey() }

    for (i in 1..20) {
        monkeys1.forEach { it.inspectItems(monkeys1, 3) }
    }

    for (i in 1..10_000) {
        monkeys2.forEach { it.inspectItems(monkeys2, 1) }
    }

    val monkeyBusiness1 = monkeys1.map { it.inspectedItemCount }.sortedDescending().take(2).reduce(Long::times)
    val monkeyBusiness2 = monkeys2.map { it.inspectedItemCount }.sortedDescending().take(2).reduce(Long::times)

    println("Part one: $monkeyBusiness1")
    println("Part two: $monkeyBusiness2")
}

private fun String.toMonkey(): Monkey {
    val lines = this.split("\n")

    val startingItems = Regex("Starting items: (.+)").find(lines[1].trim())!!
    val (itemList) = startingItems.destructured
    val items = itemList.split(", ").map { it.toLong() }.toMutableList()

    val operation = Regex("Operation: new = (.+) (.+) (.+)").find(lines[2].trim())!!
    val (param1, op, param2) = operation.destructured
    val worryChangeFn = { it: Long -> performOperation(it, param1, op, param2) }

    val testNum = lines[3].split(" ").last().toLong()
    val testTrueMonkey = lines[4].split(" ").last().toInt()
    val testFalseMonkey = lines[5].split(" ").last().toInt()

    return Monkey(items, worryChangeFn, testNum, testTrueMonkey, testFalseMonkey)
}

private fun performOperation(old: Long, param1: String, op: String, param2: String): Long {
    val value1 = if (param1 == "old") old else param1.toLong()
    val value2 = if (param2 == "old") old else param2.toLong()

    return if (op == "+") value1 + value2 else value1 * value2
}

class Monkey(
    private val items: MutableList<Long>,
    private val worryChangeFn: (Long) -> Long,
    private val testNum: Long,
    private val testTrueMonkey: Int,
    private val testFalseMonkey: Int
) {
    var inspectedItemCount = 0L
        private set

    fun inspectItems(monkeys: List<Monkey>, worryDivider: Long) {
        val totalMod = monkeys.map { it.testNum }.reduce(Long::times)

        items.forEach {
            val newLevel = worryChangeFn(it) / worryDivider

            if (newLevel % testNum == 0L) {
                monkeys[testTrueMonkey].items.add(newLevel % totalMod)
            } else {
                monkeys[testFalseMonkey].items.add(newLevel % totalMod)
            }

            inspectedItemCount++
        }

        items.clear()
    }
}