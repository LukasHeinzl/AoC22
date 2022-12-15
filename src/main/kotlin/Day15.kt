import java.nio.file.Paths
import kotlin.math.abs

fun main() {
    val input = Paths.get("src/main/resources/day15.txt").toFile().readText()
    val sensors = input.split("\n").filter { it.isNotEmpty() }.map { it.toSensor() }

    val minX = sensors.minOf { it.x } - sensors.maxOf { it.maxDistance }
    val maxX = sensors.maxOf { it.x } + sensors.maxOf { it.maxDistance }
    val yToCheck = 2000000
    var withinRangeCount = 0

    for (x in minX..maxX) {
        if (sensors.count { it.distanceTo(x, yToCheck) <= it.maxDistance } > 0) {
            if (sensors.count { it.beaconX == x && it.beaconY == yToCheck } == 0) {
                withinRangeCount++
            }
        }
    }

    println("Part one: $withinRangeCount")

    val maxDistance = 4000000

    outer@ for (y in 0..maxDistance) {
        var x = 0
        while (x <= maxDistance) {
            val sensor = sensors.find { it.distanceTo(x, y) <= it.maxDistance }

            if (sensor == null) {
                println("Part two: ${x.toLong() * 4000000 + y}")
                break@outer
            }

            x = sensor.maxColumnInRow(y) + 1
        }
    }
}

private fun String.toSensor(): Sensor {
    val match = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)").find(this)!!
    val (x, y, beaconX, beaconY) = match.destructured

    return Sensor(x.toInt(), y.toInt(), beaconX.toInt(), beaconY.toInt())
}

class Sensor(val x: Int, val y: Int, val beaconX: Int, val beaconY: Int) {
    val maxDistance: Int get() = distanceTo(beaconX, beaconY)

    fun distanceTo(x: Int, y: Int) = abs(this.x - x) + abs(this.y - y)

    fun maxColumnInRow(y: Int) = x + abs(maxDistance - abs(this.y - y))

    override fun toString(): String = "($x, $y) -> ($beaconX, $beaconY)"
}