import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val input = Paths.get("src/main/resources/day18.txt").toFile().readText()
    val cubes = input.split("\n").filter { it.isNotEmpty() }.map { it.toCube() }
    cubes.forEach { it.calculateConnections(cubes) }

    val totalSurfaceArea = cubes.sumOf { it.surfaceArea }
    val actualSurfaceArea = cubes.getSurfaceArea()
    println("Part one: $totalSurfaceArea")
    println("Part two: $actualSurfaceArea")
}

private fun List<Cube>.getSurfaceArea(): Int {
    val minX = this.minOf { it.x } - 1
    val maxX = this.maxOf { it.x } + 1
    val minY = this.minOf { it.y } - 1
    val maxY = this.maxOf { it.y } + 1
    val minZ = this.minOf { it.z } - 1
    val maxZ = this.maxOf { it.z } + 1

    val visited = mutableListOf<Cube>()
    val queue = mutableListOf(Cube(minX, minY, minZ))
    var surfaceCount = 0

    while (queue.isNotEmpty()) {
        val cube = queue.removeFirst()

        if (cube in visited) {
            continue
        }

        visited.add(cube)

        cube.getNeighbors().filter {
            it.x in minX..maxX && it.y in minY..maxY && it.z in minZ..maxZ && it !in visited
        }.forEach {
            if (it in this) {
                surfaceCount++
            } else {
                queue.add(it)
            }
        }
    }

    return surfaceCount
}

private fun String.toCube(): Cube {
    val parts = this.split(",")
    return Cube(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
}

data class Cube(val x: Int, val y: Int, val z: Int) {

    private var connections = 0
    val surfaceArea: Int get() = 6 - connections

    fun getNeighbors(): List<Cube> {
        val deltas = listOf(
            Cube(-1, 0, 0), Cube(1, 0, 0),
            Cube(0, -1, 0), Cube(0, 1, 0),
            Cube(0, 0, -1), Cube(0, 0, 1)
        )

        return deltas.map { this + it }
    }

    operator fun plus(other: Cube) = Cube(x + other.x, y + other.y, z + other.z)

    fun calculateConnections(others: List<Cube>) {
        connections = others.count { isConnected(it) }
    }

    private fun isConnected(other: Cube): Boolean {
        val dx = abs(x - other.x)
        val dy = abs(y - other.y)
        val dz = abs(z - other.z)

        return dx + dy + dz == 1
    }
}