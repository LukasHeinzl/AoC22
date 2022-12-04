import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day1.txt").toFile().readText()

    val inputPerElf = input.split("\n\n")
    val caloriesPerElf = inputPerElf.map {
        it.split("\n")
            .filter { line -> line.isNotEmpty() }
            .sumOf { line -> line.toInt() }
    }

    println("Calories for top elf: ${caloriesPerElf.max()}")
    println("Calories for top three elves: ${caloriesPerElf.sortedDescending().take(3).sum()}")
}