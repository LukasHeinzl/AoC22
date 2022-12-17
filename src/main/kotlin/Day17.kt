import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day17.txt").toFile().readText()

    val movement = input.split("\n")[0]

    val part1 = calculateCycle(movement, 2022)
    println(part1.second)

    // Part two
    // calculation order and idea credit: https://github.com/SimonBaars/AdventOfCode-Java/blob/master/src/main/java/com/sbaars/adventofcode/year22/days/Day17.java
    val startCycle = calculateCycle(movement, Long.MAX_VALUE, 1)
    val secondCycle = calculateCycle(movement, Long.MAX_VALUE, 2)
    val rocksPerCycle = secondCycle.first - startCycle.first
    val heightPerCycle = secondCycle.second - startCycle.second

    val targetRocks = 1_000_000_000_000
    val neededCycles = targetRocks / rocksPerCycle
    val totalRocks = rocksPerCycle * neededCycles + startCycle.first
    val totalHeight = heightPerCycle * neededCycles + startCycle.second
    val overshoot = totalRocks - targetRocks
    val heightAtOvershoot = calculateCycle(movement, startCycle.first - overshoot)

    println(totalHeight - (startCycle.second - heightAtOvershoot.second))
    println(totalRocks - overshoot)
}

private fun calculateCycle(movement: String, maxDrops: Long, maxCycles: Int = Int.MAX_VALUE): Pair<Long, Long> {
    val possibleRocks = listOf(Rock.dash, Rock.plus, Rock.j, Rock.line, Rock.block)
    val rocks = mutableListOf(Rock.dash.copy())
    var currentRock = rocks[0]
    var currentMove = 0
    var currentRockShape = 0
    var droppedRocks = 0L
    var cycles = 0

    while (droppedRocks < maxDrops) {
        val nextMove = movement[currentMove]
        val dx = if (nextMove == '<') -1 else 1
        currentRock.x += dx

        if (currentRock.x < 0 || currentRock.x + currentRock.width > 7 || rocks.count {
                it != currentRock && it.collidesWith(
                    currentRock
                )
            } > 0) {
            currentRock.x -= dx
        }

        currentMove++

        if (currentMove == movement.length) {
            currentMove %= movement.length
            cycles++

            if (cycles == maxCycles) {
                break
            }
        }

        currentRock.y--

        if (currentRock.y < 0 || rocks.count { it != currentRock && it.collidesWith(currentRock) } > 0) {
            currentRock.y++
            currentRockShape++
            currentRockShape %= possibleRocks.count()

            currentRock = possibleRocks[currentRockShape].copy()
            val currentMaxHeight = rocks.maxOf { it.y + it.height }
            currentRock.y += currentMaxHeight
            rocks.add(currentRock)
            droppedRocks++
        }
    }

    rocks.removeLast()
    return Pair(droppedRocks, rocks.maxOf { it.y + it.height })
}

class Rock(
    var x: Int,
    var y: Long,
    val width: Int,
    val height: Int,
    private val parts: List<Pair<Int, Int>>
) {

    private val currentPositionParts get() = parts.map { Pair(it.first + x, it.second + y) }

    fun collidesWith(other: Rock) = currentPositionParts.count { other.currentPositionParts.contains(it) } > 0

    fun copy() = Rock(x, y, width, height, parts)

    companion object {
        val dash = Rock(2, 3, 4, 1, listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0)))
        val plus = Rock(2, 3, 3, 3, listOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(1, 2)))
        val j = Rock(2, 3, 3, 3, listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(2, 1), Pair(2, 2)))
        val line = Rock(2, 3, 1, 4, listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3)))
        val block = Rock(2, 3, 2, 2, listOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1)))
    }
}