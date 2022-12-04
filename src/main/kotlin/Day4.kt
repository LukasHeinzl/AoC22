import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day4.txt").toFile().readText()

    val assignments = input.split("\n").filter { it.isNotEmpty() }.map { Assignment(it) }
    val overlapCountPart1 = assignments.count { it.hasOverlap }
    println("Total count part one: $overlapCountPart1")

    val overlapCountPart2 = assignments.count { it.hasPartialOverlap }
    println("Total count part two: $overlapCountPart2")
}

class Bound(private val lower: Int, private val upper: Int) {
    fun fullyContains(other: Bound): Boolean = other.lower >= this.lower && other.upper <= this.upper
    fun partiallyContains(other: Bound): Boolean =
        (other.lower >= this.lower && other.lower <= this.upper) ||
                (other.upper >= this.lower && other.upper <= this.upper)
}

class Assignment(input: String) {

    private val elf1: Bound
    private val elf2: Bound

    val hasOverlap: Boolean get() = elf1.fullyContains(elf2) || elf2.fullyContains(elf1)
    val hasPartialOverlap: Boolean get() = elf1.partiallyContains(elf2) || elf2.partiallyContains(elf1)

    init {
        val elves = input.split(",")
        val elf1Parts = elves[0].split("-")
        val elf2Parts = elves[1].split("-")
        elf1 = Bound(elf1Parts[0].toInt(), elf1Parts[1].toInt())
        elf2 = Bound(elf2Parts[0].toInt(), elf2Parts[1].toInt())
    }

}