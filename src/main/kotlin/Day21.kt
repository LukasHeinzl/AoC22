import java.nio.file.Paths
import kotlin.math.round

fun main() {
    val input = Paths.get("src/main/resources/day21.txt").toFile().readText()

    val lines = input.split("\n").filter { it.isNotEmpty() }
    val data = lines.toMonkeyMath().toMutableMap()
    val rootNode = data["root"]!!
    val partOne = rootNode.performOperation(data)

    println("Part one: $partOne")

    val rootDependencies = rootNode.dependencies.map { data[it]!! }
    val myName = "humn"
    val lhsContainsMyValue = rootDependencies[0].getDependenciesRecursive(data).contains(myName)
    val valueToMatch =
        if (lhsContainsMyValue) rootDependencies[1].performOperation(data)
        else rootDependencies[0].performOperation(data)

    val myNewValue =
        (if (lhsContainsMyValue) rootDependencies[0] else rootDependencies[1]).solveFor(myName, valueToMatch, data)

    println("Part two: $myNewValue")
}

private fun List<String>.toMonkeyMath(): Map<String, MathOperation> {
    val data = mutableMapOf<String, MathOperation>()

    this.forEach {
        val parts = it.split(": ")

        data[parts[0]] = if (parts[1].contains(Regex("[+\\-*/]"))) {
            val match = Regex("(.*) ([+\\-*/]) (.*)").find(parts[1])!!
            val (expr1, op, expr2) = match.destructured
            MathOperation(expr1, op, expr2)
        } else {
            MathOperation(parts[1])
        }
    }

    return data
}

private fun String.performReverseOperation(val1: Long, val2: Long, lhs: Boolean) = when (this) {
    "+" -> val1 - val2
    "-" -> if (lhs) val1 + val2 else val2 - val1
    "*" -> round(val1.toDouble() / val2).toLong()
    "/" -> if (lhs) val1 * val2 else round(val2.toDouble() / val1).toLong()
    else -> error("Invalid math operator: $this")
}

data class MathOperation(val value1: String, val op: String? = null, val value2: String? = null) {

    val dependencies get() = listOfNotNull(value1, value2)
    private val recursiveCache = mutableListOf<String>()

    fun getDependenciesRecursive(data: Map<String, MathOperation>): List<String> {
        if (recursiveCache.isNotEmpty()) {
            return recursiveCache
        }

        val dependencies = mutableListOf(value1)

        if (value1.toIntOrNull() == null) {
            dependencies.addAll(data[value1]!!.getDependenciesRecursive(data))
        }

        if (value2 != null) {
            dependencies.add(value2)

            if (value2.toIntOrNull() == null) {
                dependencies.addAll(data[value2]!!.getDependenciesRecursive(data))
            }
        }

        recursiveCache.addAll(dependencies)
        return dependencies
    }

    fun performOperation(data: Map<String, MathOperation>): Long {
        val val1 = value1.toLongOrNull() ?: data[value1]!!.performOperation(data)
        val val2 = value2?.toLongOrNull() ?: data[value2]?.performOperation(data) ?: return val1

        return when (op) {
            "+" -> val1 + val2
            "-" -> val1 - val2
            "*" -> val1 * val2
            "/" -> round(val1.toDouble() / val2).toLong()
            else -> error("Invalid math operator: $op")
        }
    }

    fun solveFor(name: String, valueToMatch: Long, data: Map<String, MathOperation>): Long {
        if (value1.toLongOrNull() != null) {
            return value1.toLong()
        }

        val lhsDependencies = data[value1]!!.getDependenciesRecursive(data)
        val lhsContainsName = lhsDependencies.contains(name) || value1 == name

        val lhs = data[value1]!!.performOperation(data)
        val rhs = data[value2]!!.performOperation(data)
        val newValueToMatch =
            op!!.performReverseOperation(valueToMatch, if (lhsContainsName) rhs else lhs, lhsContainsName)

        return if (value1 == name || value2 == name) {
            newValueToMatch
        } else {
            (if (lhsContainsName) data[value1] else data[value2])!!.solveFor(name, newValueToMatch, data)
        }
    }
}