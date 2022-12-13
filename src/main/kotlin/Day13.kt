import java.nio.file.Paths
import kotlinx.serialization.json.*

fun main() {
    val input = Paths.get("src/main/resources/day13.txt").toFile().readText()

    val pairs = input.split("\n\n").map { it.split("\n") }.map { Pair(it[0].toPacket(), it[1].toPacket()) }
    var sum = 0

    for (i in 0..pairs.lastIndex) {
        if (pairs[i].first.compare(pairs[i].second) >= 0) {
            sum += i + 1
        }
    }

    println("Part one: $sum")

    val packets = pairs.map { listOf(it.first, it.second) }.flatten().toMutableList()
    val divider1 = Packet(ListEntry(listOf(IntEntry(2))))
    val divider2 = Packet(ListEntry(listOf(IntEntry(6))))

    packets.addAll(listOf(divider1, divider2))
    packets.sortWith { a, b -> -a.compare(b) }

    val dividerIdx1 = packets.indexOf(divider1) + 1
    val dividerIdx2 = packets.indexOf(divider2) + 1
    println("Part two: ${dividerIdx1 * dividerIdx2}")
}

private fun String.toPacket(): Packet = Packet(Json.parseToJsonElement(this).jsonArray.toPacketEntry() as ListEntry)

private fun JsonElement.toPacketEntry(): Entry {
    return try {
        IntEntry(this.jsonPrimitive.int)
    } catch (_: Exception) {
        ListEntry(this.jsonArray.map { it.toPacketEntry() })
    }
}

open class Entry

class IntEntry(val value: Int) : Entry() {
    override fun toString() = value.toString()
}

class ListEntry(private val data: List<Entry>) : Entry() {

    override fun toString(): String = data.toString()

    fun compare(other: ListEntry): Int {
        for (i in 0..data.lastIndex) {
            if (i > other.data.lastIndex) {
                return -1
            }

            val leftValue = data[i]
            val rightValue = other.data[i]

            if (leftValue is IntEntry && rightValue is IntEntry) {
                if (leftValue.value < rightValue.value) {
                    return 1
                } else if (leftValue.value > rightValue.value) {
                    return -1
                }
            } else if (leftValue is ListEntry && rightValue is ListEntry) {
                val result = leftValue.compare(rightValue)

                if (result != 0) {
                    return result
                }
            } else if (leftValue is IntEntry && rightValue is ListEntry) {
                val result = ListEntry(listOf(leftValue)).compare(rightValue)

                if (result != 0) {
                    return result
                }
            } else if (leftValue is ListEntry && rightValue is IntEntry) {
                val result = leftValue.compare(ListEntry(listOf(rightValue)))

                if (result != 0) {
                    return result
                }
            }
        }

        return other.data.count() - data.count()
    }
}

class Packet(private val data: ListEntry) {

    override fun toString(): String = data.toString()

    fun compare(other: Packet): Int = data.compare(other.data)
}