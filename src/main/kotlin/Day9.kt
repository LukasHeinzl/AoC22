import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val input = Paths.get("src/main/resources/day9.txt").toFile().readText()

    val moves = input.split("\n").filter { it.isNotEmpty() }
    val tail = RopeEnd()
    val head = RopeEnd(tail)

    moves.forEach {
        val move = it.toMove()
        head.move(move.first, move.second)
    }

    println("Part one: ${tail.visitedPosition.distinct().count()}")

    val knots = mutableListOf(RopeEnd())

    for (i in 1 until 10) {
        knots.add(RopeEnd(knots[i - 1]))
    }

    val head2 = knots.last()
    val tail2 = knots.first()

    moves.forEach {
        val move = it.toMove()
        head2.move(move.first, move.second)
    }

    println("Part two: ${tail2.visitedPosition.distinct().count()}")
}

private fun String.toMove(): Pair<Direction, Int> {
    val parts = this.split(" ")

    return Pair(parts[0].toDirection(), parts[1].toInt())
}

private fun String.toDirection(): Direction = when (this) {
    "U" -> Direction.UP
    "R" -> Direction.RIGHT
    "D" -> Direction.DOWN
    "L" -> Direction.LEFT
    else -> throw Exception("should not happen")
}

enum class Direction(val xFactor: Int, val yFactor: Int) {
    UP(0, 1),
    RIGHT(1, 0),
    DOWN(0, -1),
    LEFT(-1, 0)
}

class RopeEnd(private val linkedEnd: RopeEnd? = null) {
    private var x: Int = 0
    private var y: Int = 0

    val visitedPosition = mutableListOf(Pair(0, 0))

    fun move(direction: Direction, amount: Int) {
        for (i in 0 until amount) {
            x += direction.xFactor
            y += direction.yFactor

            visitedPosition.add(Pair(x, y))
            linkedEnd?.adjustToLinkedEnd(this)
        }
    }

    private fun adjustToLinkedEnd(otherEnd: RopeEnd) {
        val dx = otherEnd.x - x
        val dy = otherEnd.y - y

        if ((dx == 0 && dy == 0) || (abs(dx) == 1 && dy == 0) || (dx == 0 && abs(dy) == 1) || (abs(dx) == 1 && abs(dy) == 1)) {
            return
        }

        if (dx > 0) {
            x += 1
        } else if (dx < 0) {
            x -= 1
        }

        if (dy > 0) {
            y += 1
        } else if (dy < 0) {
            y -= 1
        }

        visitedPosition.add(Pair(x, y))
        linkedEnd?.adjustToLinkedEnd(this)
    }
}