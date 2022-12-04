import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day3.txt").toFile().readText()

    val rucksacks = input.split("\n").filter { it.isNotEmpty() }.map { Rucksack.fromString(it) }
    val duplicates = rucksacks.map { it.getDuplicates() }.flatten()
    val priorities = duplicates.map { it.getPriority() }
    println("Total score part one: ${priorities.sum()}")

    val badges = mutableListOf<Char>()
    for (i in 0..rucksacks.lastIndex step 3) {
        val rucksack1Items = rucksacks[i].allItems
        val rucksack2Items = rucksacks[i + 1].allItems
        val rucksack3Items = rucksacks[i + 2].allItems
        val itemsInAll = rucksack1Items.intersect(rucksack2Items.toSet()).intersect(rucksack3Items.toSet())
        badges.addAll(itemsInAll)
    }

    val badgePriorities = badges.map { it.getPriority() }
    println("Total score part two: ${badgePriorities.sum()}")
}

private fun Char.getPriority(): Int {
    if (this.isUpperCase()) {
        return this - 'A' + 27
    }

    return this - 'a' + 1
}

class Rucksack(
    private val itemsCompartment1: CharArray,
    private val itemsCompartment2: CharArray
) {

    fun getDuplicates() =
        itemsCompartment1.distinct().intersect(itemsCompartment2.distinct().toSet())

    val allItems get() = listOf(itemsCompartment1.toList(), itemsCompartment2.toList()).flatten()

    companion object {
        fun fromString(s: String): Rucksack {
            val half = s.length / 2
            val compartment1 = s.substring(0, half)
            val compartment2 = s.substring(half)

            return Rucksack(compartment1.toCharArray(), compartment2.toCharArray())
        }
    }
}