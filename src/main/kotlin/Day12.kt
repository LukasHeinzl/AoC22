import java.nio.file.Paths
import kotlin.math.abs
import kotlin.math.sqrt

fun main() {
    val input = Paths.get("src/main/resources/day12.txt").toFile().readText()

    val lines = input.split("\n").filter { it.isNotEmpty() }
    val locations = mutableListOf<Location>()

    for (y in 0..lines.lastIndex) {
        for (x in 0..lines[y].lastIndex) {
            val loc = Location(x, y, lines[y][x].toLevel(), lines[y][x] == 'S', lines[y][x] == 'E')
            val potentialLeft = locations.find { it.x == x - 1 && it.y == y }
            val potentialTop = locations.find { it.x == x && it.y == y - 1 }

            if (potentialLeft != null) {
                loc.linkLeft(potentialLeft)
            }

            if (potentialTop != null) {
                loc.linkTop(potentialTop)
            }

            locations.add(loc)
        }
    }

    val start = locations.find { it.isStart }!!
    val end = locations.find { it.isEnd }!!
    val algo = AStar(start, end)
    val path = algo.findPath()

    println("Part one: ${path.count() - 1}")

    val potentialStarts = locations.filter { it.level == 0 }
    val minDistance = potentialStarts.map { AStar(it, end).findPath().count() - 1 }.filter { it > 0 }.min()

    println("Part two: $minDistance")
}

private fun Char.toLevel(): Int = when (this) {
    'S' -> 0
    'E' -> 25
    else -> this - 'a'
}

data class Location(val x: Int, val y: Int, val level: Int, val isStart: Boolean, val isEnd: Boolean) {
    private var leftLink: Location? = null
    private var topLink: Location? = null
    private var rightLink: Location? = null
    private var bottomLink: Location? = null

    val links: List<Location> get() = listOfNotNull(leftLink, topLink, rightLink, bottomLink)

    fun linkLeft(other: Location) {
        if (other.level - level <= 1) {
            leftLink = other
        }

        if (level - other.level <= 1) {
            other.rightLink = this
        }
    }

    fun linkTop(other: Location) {
        if (other.level - level <= 1) {
            topLink = other
        }

        if (level - other.level <= 1) {
            other.bottomLink = this
        }
    }

    fun getDistance(other: Location): Int {
        val dx = abs(x - other.x).toDouble()
        val dy = abs(y - other.y)
        return sqrt(dx * dx + dy * dy).toInt()
    }
}

class AStar(start: Location, private val end: Location) {
    private val queue = mutableListOf<Location>()
    private val backtrackMap = mutableMapOf<Location, Location?>()
    private val visited = mutableListOf<Location>()
    private val totalDistMap = mutableMapOf<Location, Int>()
    private val startDistMap = mutableMapOf<Location, Int>()

    init {
        backtrackMap[start] = null
        totalDistMap[start] = start.getDistance(end)
        startDistMap[start] = 0
        queue.add(start)
    }

    fun findPath(): List<Location> {
        while (queue.isNotEmpty()) {
            queue.sortBy { totalDistMap[it] }

            val currentNode = queue[0]

            if (currentNode == end) {
                return getPath(end)
            }

            queue.remove(currentNode)
            visited.add(currentNode)

            for (link in currentNode.links) {
                if (visited.contains(link)) {
                    continue
                }

                if (!queue.contains(link)) {
                    queue.add(link)
                }

                val dist = startDistMap[currentNode]!! + currentNode.getDistance(link)

                if (startDistMap.contains(link) && dist >= startDistMap[link]!!) {
                    continue
                }

                backtrackMap[link] = currentNode
                startDistMap[link] = dist
                totalDistMap[link] = dist + link.getDistance(end)
            }
        }

        return listOf()
    }

    private fun getPath(node: Location): List<Location> {
        val path = mutableListOf<Location>()
        var currentNode: Location? = node

        while (currentNode != null) {
            path.add(0, currentNode)
            currentNode = backtrackMap[currentNode]
        }

        return path
    }
}