import java.nio.file.Paths

fun main() {
    val input = Paths.get("src/main/resources/day7.txt").toFile().readText()

    val lines = input.split("\n").filter { it.isNotEmpty() }
    val nodes = mutableListOf(Node("/", NodeType.DIRECTORY))
    var currentPath = ""
    var currentDirectory: Node? = null

    for (i in 0..lines.lastIndex) {
        if (lines[i].startsWith("$")) {
            val cdPath = lines[i].getCdPath() ?: continue
            currentPath = currentPath.cd(cdPath)
            currentDirectory = nodes.find { it.name == currentPath }
        } else {
            val newNode = lines[i].toNode(currentPath)
            val existingNode = nodes.find { it.name == newNode.name }
            currentDirectory?.addChild(existingNode ?: newNode)

            if (existingNode == null) {
                nodes.add(newNode)
            }
        }
    }

    val totalSizePart1 = nodes
        .filter { it.getTotalSize() < 100_000 && it.type == NodeType.DIRECTORY }
        .sumOf { it.getTotalSize() }
    println("Total part one: $totalSizePart1")

    val totalDiskSize = 70_000_000
    val neededSize = 30_000_000
    val usedSpace = nodes[0].getTotalSize()
    val freeSpace = totalDiskSize - usedSpace
    val neededToDelete = neededSize - freeSpace

    val directoryToDelete = nodes
        .filter { it.type == NodeType.DIRECTORY && it.getTotalSize() >= neededToDelete }
        .minByOrNull { it.getTotalSize() }!!

    println("Part two: ${directoryToDelete.getTotalSize()}")
}

private fun String.getCdPath(): String? {
    val parts = this.split(" ")

    if (parts[1] != "cd") return null

    return parts[2]
}

private fun String.cd(dir: String): String = when (dir) {
    "/" -> "/"
    ".." -> this.substring(0, this.lastIndexOf('/'))
    else -> if (this == "/") "$this$dir" else "$this/$dir"
}

private fun String.toNode(currentPath: String): Node {
    val parts = this.split(" ")
    val fullPath = if (currentPath == "/") "/${parts[1]}" else "$currentPath/${parts[1]}"

    if (parts[0] == "dir") {
        return Node(fullPath, NodeType.DIRECTORY)
    }

    return Node(fullPath, NodeType.FILE, parts[0].toInt())
}

enum class NodeType {
    DIRECTORY, FILE
}

data class Node(val name: String, val type: NodeType, private val fileSize: Int? = null) {

    private val children = mutableListOf<Node>()

    fun getTotalSize(): Int = if (type == NodeType.FILE) fileSize!! else children.sumOf { it.getTotalSize() }

    fun addChild(child: Node) {
        children.add(child)
    }
}