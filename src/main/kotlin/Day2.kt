import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day2.txt").toFile().readText()

    val rounds = input.split("\n").filter { it.isNotEmpty() }
    val roundShapes = rounds.map { it.split(" ").map { s -> Shape.fromString(s) } }
    val roundScoresPart1 = roundShapes.map { calculateScore(it[0], it[1]) }
    println("Total score part one: ${roundScoresPart1.sum()}")

    val roundScoresPart2 = rounds.map {
        val parts = it.split(" ")
        val opponentShape = Shape.fromString(parts[0])
        val myShape = opponentShape.getShapeForDesiredOutcome(Outcome.fromString(parts[1]))
        calculateScore(opponentShape, myShape)
    }
    println("Total score part two: ${roundScoresPart2.sum()}")
}

fun calculateScore(opponentShape: Shape, myShape: Shape): Int {
    val scoreForShape = when (myShape) {
        Shape.ROCK -> 1
        Shape.PAPER -> 2
        Shape.SCISSORS -> 3
    }

    val scoreForRoundOutcome = myShape.getScoreAgainst(opponentShape)

    return scoreForShape + scoreForRoundOutcome
}

enum class Outcome {
    LOSS, DRAW, WIN;

    companion object {
        fun fromString(name: String): Outcome = when (name) {
            "X" -> LOSS
            "Y" -> DRAW
            "Z" -> WIN
            else -> LOSS // should not happen in this example - just ignore it
        }
    }
}

enum class Shape {
    ROCK, PAPER, SCISSORS;

    fun getScoreAgainst(other: Shape): Int {
        return when (this) {
            ROCK -> when (other) {
                PAPER -> 0
                ROCK -> 3
                SCISSORS -> 6
            }

            PAPER -> when (other) {
                PAPER -> 3
                ROCK -> 6
                SCISSORS -> 0
            }

            SCISSORS -> when (other) {
                PAPER -> 6
                ROCK -> 0
                SCISSORS -> 3
            }
        }
    }

    fun getShapeForDesiredOutcome(outcome: Outcome): Shape = when (this) {
        ROCK -> when (outcome) {
            Outcome.LOSS -> SCISSORS
            Outcome.DRAW -> ROCK
            Outcome.WIN -> PAPER
        }

        PAPER -> when (outcome) {
            Outcome.LOSS -> ROCK
            Outcome.DRAW -> PAPER
            Outcome.WIN -> SCISSORS
        }

        SCISSORS -> when (outcome) {
            Outcome.LOSS -> PAPER
            Outcome.DRAW -> SCISSORS
            Outcome.WIN -> ROCK
        }
    }

    companion object {
        fun fromString(name: String): Shape = when (name) {
            "A" -> ROCK
            "X" -> ROCK
            "B" -> PAPER
            "Y" -> PAPER
            "C" -> SCISSORS
            "Z" -> SCISSORS
            else -> ROCK // should not happen in this example - just ignore it
        }
    }
}