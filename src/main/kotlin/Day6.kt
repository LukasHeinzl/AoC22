import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day6.txt").toFile().readText()

    val charsReceived = input.toCharArray()

    println("Part one: ${charsReceived.indexOfNCharacters(4)}")
    println("Part two: ${charsReceived.indexOfNCharacters(14)}")
}

private fun CharArray.indexOfNCharacters(n: Int): Int {
    for (i in 0 until this.lastIndex - n) {
        if (this.sliceArray(i until i + n).distinct().count() == n) {
            return i + n
        }
    }

    return -1
}