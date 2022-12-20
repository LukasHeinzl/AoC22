import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day20.txt").toFile().readText()

    val numbers = input.split("\n")
        .filter { it.isNotEmpty() }
        .mapIndexed { idx, it -> EntryPositionAndValue(idx, it.toLong()) }
    val partOne = numbers.mixed().map { it.value }.groveCoordinate()

    println("Part one: $partOne")

    val key = 811589153
    val rounds = 10
    val partTwo = numbers.map { EntryPositionAndValue(it.pos, it.value * key) }
        .mixed(rounds)
        .map { it.value }.groveCoordinate()

    println("Part two: $partTwo")
}

data class EntryPositionAndValue(val pos: Int, val value: Long)

private fun List<Long>.groveCoordinate(): Long {
    val count = this.count()
    val idxOfZero = this.indexOf(0)
    val num1000th = this[(1000 + idxOfZero) % count]
    val num2000th = this[(2000 + idxOfZero) % count]
    val num3000th = this[(3000 + idxOfZero) % count]

    return num1000th + num2000th + num3000th
}

private fun List<EntryPositionAndValue>.mixed(rounds: Int = 1): List<EntryPositionAndValue> {
    val data = this.toMutableList()
    val maxIdx = data.count() - 1

    for (i in 0 until rounds) {
        for (number in this) {
            val fromIdx = data.indexOf(number)
            val toIdx = Math.floorMod(fromIdx + number.value, maxIdx)
            data.add(toIdx, data.removeAt(fromIdx))
        }
    }

    return data
}