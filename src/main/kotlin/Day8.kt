import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day8.txt").toFile().readText()

    val lines = input.split("\n").filter { it.isNotEmpty() }
    val height = lines.count()
    val width = lines[0].count()

    val trees = Array(width) { IntArray(height) }

    for (y in 0 until height) {
        for (x in 0 until width) {
            trees[x][y] = lines[y][x] - '0'
        }
    }

    var visibleCount = 0
    var maxScore = -1

    for (x in 0 until width) {
        for (y in 0 until height) {
            if (isVisible(x, y, trees)) {
                visibleCount++
            }

            val score = getScore(x, y, trees)

            if (score > maxScore) {
                maxScore = score
            }
        }
    }

    println("Part one: $visibleCount")
    println("Part two: $maxScore")
}

private fun isVisible(x: Int, y: Int, data: Array<IntArray>): Boolean {
    val height = data.count()
    val width = data[0].count()

    val myHeight = data[x][y]

    if (x == 0 || y == 0 || x == width - 1 || y == height - 1) return true

    var isVisible = true

    for (dx in x - 1 downTo 0) {
        if (data[dx][y] >= myHeight) {
            isVisible = false
            break
        }
    }

    if (isVisible) return true

    isVisible = true

    for (dx in x + 1 until width) {
        if (data[dx][y] >= myHeight) {
            isVisible = false
            break
        }
    }

    if (isVisible) return true

    isVisible = true

    for (dy in y - 1 downTo 0) {
        if (data[x][dy] >= myHeight) {
            isVisible = false
            break
        }
    }

    if (isVisible) return true

    isVisible = true

    for (dy in y + 1 until height) {
        if (data[x][dy] >= myHeight) {
            isVisible = false
            break
        }
    }

    return isVisible
}

private fun getScore(x: Int, y: Int, data: Array<IntArray>): Int {
    val height = data.count()
    val width = data[0].count()

    val myHeight = data[x][y]

    if (x == 0 || y == 0 || x == width - 1 || y == height - 1) return 0

    var score1 = 0
    var score2 = 0
    var score3 = 0
    var score4 = 0

    for (dx in x - 1 downTo 0) {
        score1++
        if (data[dx][y] >= myHeight) {
            break
        }
    }

    for (dx in x + 1 until width) {
        score2++
        if (data[dx][y] >= myHeight) {
            break
        }
    }

    for (dy in y - 1 downTo 0) {
        score3++
        if (data[x][dy] >= myHeight) {
            break
        }
    }

    for (dy in y + 1 until height) {
        score4++
        if (data[x][dy] >= myHeight) {
            break
        }
    }

    return score1 * score2 * score3 * score4
}