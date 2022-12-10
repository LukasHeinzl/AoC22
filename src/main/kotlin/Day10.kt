import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day10.txt").toFile().readText()

    val instructions = input.split("\n").filter { it.isNotEmpty() }.map { it.toInstruction() }
    var totalSignalStrength = 0
    var currentCycle = 1

    println("Part two:")

    for (i in 0..instructions.lastIndex) {
        while (!instructions[i].isDone) {
            // part two - CRT drawing
            val xPos = (currentCycle - 1) % 40

            // line break after every 40 cycles for CRT drawing
            if (currentCycle != 1 && xPos == 0) {
                println()
            }

            if (xPos >= xRegister - 1 && xPos <= xRegister + 1) {
                print("#")
            } else {
                print(".")
            }

            // execute instructions - calculate part one
            instructions[i].execute()
            currentCycle++

            if (currentCycle == 20 || (currentCycle - 20) % 40 == 0) {
                totalSignalStrength += xRegister * currentCycle
            }
        }
    }

    println()
    println("Part one: $totalSignalStrength")
}

private var xRegister = 1

private fun addToXRegister(amount: Int) {
    xRegister += amount
}

private fun String.toInstruction(): Instruction {
    val parts = this.split(" ")

    if (parts.count() == 2 && parts[0] == "addx") {
        return Instruction(InstructionType.ADD_X) { addToXRegister(parts[1].toInt()) }
    }

    return Instruction(InstructionType.NOOP)
}

enum class InstructionType(val neededCycles: Int) {
    NOOP(1), ADD_X(2)
}

class Instruction(type: InstructionType, private val fn: () -> Unit = {}) {
    private var remainingCycles = type.neededCycles
    val isDone: Boolean get() = remainingCycles == 0

    fun execute() {
        remainingCycles--

        if (remainingCycles == 0) {
            fn()
        }
    }
}