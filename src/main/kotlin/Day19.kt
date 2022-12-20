import java.nio.file.Paths
import kotlin.math.max

fun main() {
    val input = Paths.get("src/main/resources/day19.txt").toFile().readText()
    val blueprints = input.split("\n").filter { it.isNotEmpty() }.map { it.toBlueprint() }

    val part1 = blueprints.mapIndexed { idx, it -> it.simulate(BlueprintState(), mutableMapOf()) * (idx + 1) }.sum()
    println("Part one: $part1")

    val part2 = blueprints.take(3)
        .map { it.simulate(BlueprintState(timeLeft = 32), mutableMapOf()) }
        .reduce { acc, i -> acc * i }
    println("Part two: $part2")
}

data class BlueprintState(
    val timeLeft: Int = 24,
    val oreRobotCount: Int = 1,
    val clayRobotCount: Int = 0,
    val obsidianRobotCount: Int = 0,
    val geodeRobotCount: Int = 0,
    val oreCount: Int = 0,
    val clayCount: Int = 0,
    val obsidianCount: Int = 0,
    val geodeCount: Int = 0
)

private fun Blueprint.simulate(state: BlueprintState, cache: MutableMap<BlueprintState, Int>): Int {
    if (state.timeLeft == 0) {
        return state.geodeCount
    }

    if (cache.containsKey(state)) {
        return cache[state]!!
    }

    var bestCase = 0
    var canBuildNewGeodeRobot = false
    var canBuildNewObsidianRobot = false
    var canBuildNewClayRobot = false
    var canBuildNewOreRobot = false

    // spending phase
    if (state.obsidianCount >= this.geodeRobotObsidianCost && state.oreCount >= this.geodeRobotOreCost) {
        canBuildNewGeodeRobot = true
    }

    if (state.clayCount >= this.obsidianRobotClayCost && state.oreCount >= this.obsidianRobotOreCost) {
        if (shouldMakeMoreRobot(
                state.obsidianRobotCount,
                state.obsidianCount,
                state.timeLeft,
                this.geodeRobotObsidianCost
            )
        ) {
            canBuildNewObsidianRobot = true
        }
    }

    if (state.oreCount >= this.clayRobotCost) {
        if (shouldMakeMoreRobot(
                state.clayRobotCount,
                state.clayCount,
                state.timeLeft,
                this.obsidianRobotClayCost
            )
        ) {
            canBuildNewClayRobot = true
        }
    }

    if (state.oreCount >= this.oreRobotCost) {
        if (shouldMakeMoreRobot(
                state.oreRobotCount,
                state.oreCount,
                state.timeLeft,
                maxOf(this.oreRobotCost, this.clayRobotCost, this.obsidianRobotOreCost, this.geodeRobotOreCost)
            )
        )
            canBuildNewOreRobot = true
    }

    // mining phase
    val newOreCount = state.oreCount + state.oreRobotCount
    val newClayCount = state.clayCount + state.clayRobotCount
    val newObsidianCount = state.obsidianCount + state.obsidianRobotCount
    val newGeodeCount = state.geodeCount + state.geodeRobotCount

    // building phase
    if (canBuildNewGeodeRobot) {
        bestCase = max(
            bestCase,
            simulate(
                state.copy(
                    timeLeft = state.timeLeft - 1,
                    geodeRobotCount = state.geodeRobotCount + 1,
                    oreCount = newOreCount - this.geodeRobotOreCost,
                    clayCount = newClayCount,
                    obsidianCount = newObsidianCount - this.geodeRobotObsidianCost,
                    geodeCount = newGeodeCount
                ),
                cache
            )
        )
    }

    if (canBuildNewObsidianRobot) {
        bestCase = max(
            bestCase,
            simulate(
                state.copy(
                    timeLeft = state.timeLeft - 1,
                    obsidianRobotCount = state.obsidianRobotCount + 1,
                    oreCount = newOreCount - this.obsidianRobotOreCost,
                    clayCount = newClayCount - this.obsidianRobotClayCost,
                    obsidianCount = newObsidianCount,
                    geodeCount = newGeodeCount
                ),
                cache
            )
        )
    }

    if (canBuildNewClayRobot) {
        bestCase = max(
            bestCase,
            simulate(
                state.copy(
                    timeLeft = state.timeLeft - 1,
                    clayRobotCount = state.clayRobotCount + 1,
                    oreCount = newOreCount - this.clayRobotCost,
                    clayCount = newClayCount,
                    obsidianCount = newObsidianCount,
                    geodeCount = newGeodeCount
                ),
                cache
            )
        )
    }

    if (canBuildNewOreRobot) {
        bestCase =
            max(
                bestCase, simulate(
                    state.copy(
                        timeLeft = state.timeLeft - 1,
                        oreRobotCount = state.oreRobotCount + 1,
                        oreCount = newOreCount - this.oreRobotCost,
                        clayCount = newClayCount,
                        obsidianCount = newObsidianCount,
                        geodeCount = newGeodeCount
                    ),
                    cache
                )
            )
    }

    bestCase =
        max(
            bestCase, simulate(
                state.copy(
                    timeLeft = state.timeLeft - 1,
                    oreCount = newOreCount,
                    clayCount = newClayCount,
                    obsidianCount = newObsidianCount,
                    geodeCount = newGeodeCount
                ),
                cache
            )
        )

    cache[state] = bestCase
    return bestCase
}

private fun shouldMakeMoreRobot(robotCount: Int, resourceCount: Int, timeLeft: Int, maxNeeded: Int) =
    resourceCount + robotCount * timeLeft < timeLeft * maxNeeded

private fun String.toBlueprint(): Blueprint {
    val match =
        Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.").find(
            this
        )!!
    val (id, oreRobotCost, clayRobotCost, obsidianRobotOreCost, obsidianRobotClayCost, geodeRobotOreCost, geodeRobotObsidianCost) = match.destructured

    return Blueprint(
        id.toInt(),
        oreRobotCost.toInt(),
        clayRobotCost.toInt(),
        obsidianRobotOreCost.toInt(),
        obsidianRobotClayCost.toInt(),
        geodeRobotOreCost.toInt(),
        geodeRobotObsidianCost.toInt()
    )
}

data class Blueprint(
    val id: Int,
    val oreRobotCost: Int,
    val clayRobotCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int
)