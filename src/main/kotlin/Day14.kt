import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day14.txt").toFile().readText()

    val paths = input.split("\n").filter { it.isNotEmpty() }.map { it.toPath() }
    val allPointsPart1 = paths.map { it.getAllPointsOnPath() }.flatten().toMutableList()
    var accumulatedSandPart1 = 0

    outer@ while (true) {
        val sand = SandPoint()

        while (sand.moveDown(allPointsPart1)) {
            if (sand.isProbablyFallingForever) {
                break@outer
            }
        }

        allPointsPart1.add(Point(sand.x, sand.y))
        accumulatedSandPart1++
    }

    println("Part one: $accumulatedSandPart1")

    val allPointsPart2 = paths.map { it.getAllPointsOnPath() }.flatten().toMutableList()
    val floor = allPointsPart2.maxOfOrNull { it.y }!! + 1
    var accumulatedSandPart2 = 0

    outer@ while (true) {
        val sand = SandPoint(floor)

        while (sand.moveDown(allPointsPart2)) {
            if (sand.isProbablyFallingForever) {
                break
            }
        }

        if (sand.x == 500 && sand.y == 0) {
            break@outer
        }

        allPointsPart2.add(Point(sand.x, sand.y))
        accumulatedSandPart2++
    }

    accumulatedSandPart2++
    println("Part two: $accumulatedSandPart2")
}

private fun String.toPoint(): Point {
    val parts = this.split(",")
    return Point(parts[0].toInt(), parts[1].toInt())
}

private fun String.toPath(): Path = Path(this.split(" -> ").map { it.toPoint() })

data class Point(val x: Int, val y: Int)

data class Path(val points: List<Point>) {
    fun getAllPointsOnPath(): List<Point> {
        val allPoints = mutableListOf<Point>()

        for (i in 0 until points.lastIndex) {
            val point1 = points[i]
            val point2 = points[i + 1]

            if (point1.x == point2.x) {
                if (point1.y < point2.y) {
                    for (y in point1.y..point2.y) {
                        allPoints.add(Point(point1.x, y))
                    }
                } else {
                    for (y in point2.y..point1.y) {
                        allPoints.add(Point(point1.x, y))
                    }
                }
            }

            if (point1.y == point2.y) {
                if (point1.x < point2.x) {
                    for (x in point1.x..point2.x) {
                        allPoints.add(Point(x, point1.y))
                    }
                } else {
                    for (x in point2.x..point1.x) {
                        allPoints.add(Point(x, point1.y))
                    }
                }
            }
        }

        return allPoints
    }
}

class SandPoint(private val floor: Int = 500) {

    var x: Int = 500
        private set
    var y: Int = 0
        private set

    val isProbablyFallingForever: Boolean get() = y >= floor

    fun moveDown(takenPositions: List<Point>): Boolean {
        val below = takenPositions.find { it.x == x && it.y == y + 1 }

        if (below == null) {
            y++
            return true
        }

        val belowLeft = takenPositions.find { it.x == x - 1 && it.y == y + 1 }

        if (belowLeft == null) {
            x--
            y++
            return true
        }

        val belowRight = takenPositions.find { it.x == x + 1 && it.y == y + 1 }

        if (belowRight == null) {
            x++
            y++
            return true
        }

        return false
    }

}