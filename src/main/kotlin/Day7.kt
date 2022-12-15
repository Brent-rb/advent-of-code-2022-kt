
enum class FileType {
    Directory,
    File
}

enum class Command {
    Ls,
    Cd
}

data class CommandResult(val cwd: Node, val command: Command)
class Node {
    val name: String
    val parent: Node?
    val children: MutableMap<String, Node>

    val fileType: FileType
    val fileSize: Int

    constructor(name: String, parent: Node? = null, fileType: FileType = FileType.Directory, fileSize: Int = 0) {
        this.name = name
        this.parent = parent
        this.children = mutableMapOf()
        this.fileType = fileType
        this.fileSize = fileSize
    }

    fun addChild(child: Node) {
        if (children.containsKey(child.name)) {
            throw Error("Duplicate child.")
        }

        children[child.name] = child
    }

    fun tryGetChild(childName: String): Node? {
        return children[childName]
    }

    fun getSize(): Int {
        return children.values.sumOf { it.getSize() } + fileSize
    }

    fun print(depth: Int) {
        for (i in 0..depth) {
            print("--")
        }
        println(" $name $fileSize")

        for (child in children.values) {
            child.print(depth + 1)
        }
    }
}

class Day7: Day(7) {
    lateinit var root: Node
    override fun prepare1() {
        root = constructTree()
    }

    override fun solve1(isExample: Boolean): String {
        return solvePart1(root)
    }

    override fun solve2(isExample: Boolean): String {
        return solvePart2(root)
    }

    override fun getExampleAnswer1(): String {
        TODO("Not yet implemented")
    }

    override fun getExampleAnswer2(): String {
        TODO("Not yet implemented")
    }

    private fun constructTree(): Node {
        val root = Node("/")
        var cwd = root

        for (line in lines) {
            if (line.startsWith("$")) {
                val result = parseCommand(root, cwd, line)
                cwd = result.cwd
            }
            else {
                parseFile(cwd, line)
            }
        }

        return root
    }



    private fun solvePart1(root: Node): String {
        var sum = 0

        doRecursive(root) {
            if (it.fileType != FileType.Directory) {
                return@doRecursive
            }

            val size = it.getSize()
            if (size <= 100000) {
                sum += size
            }
        }

        return "$sum"
    }

    private fun <T> doRecursive(node: Node, callback: (Node) -> T) {
        callback(node)
        for (child in node.children.values) {
            doRecursive(child, callback)
        }
    }

    private fun solvePart2(root: Node): String {
        val totalSpace = 70000000
        val neededSpace = 30000000
        val freeSpace = totalSpace - root.getSize()
        val minimumDirectorySize = neededSpace - freeSpace

        val suitableDirectories = mutableListOf<Node>()
        doRecursive(root) {
            if (it.fileType != FileType.Directory) {
                return@doRecursive
            }

            if (it.getSize() >= minimumDirectorySize) {
                suitableDirectories.add(it)
            }
        }

        suitableDirectories.sortBy { it.getSize() }
        return "${suitableDirectories[0].getSize()}"
    }


    private fun parseFile(cwd: Node, line: String) {
        val splits = line.split(" ")
        val typeInfo = splits[0]
        val fileName = splits[1]

        if (typeInfo == "dir") {
            cwd.addChild(Node(fileName, cwd))
        }
        else {
            try {
                cwd.addChild(Node(fileName, cwd, FileType.File, typeInfo.toInt()))
            }
            catch (error: Error) {
                println("Error adding child: ${typeInfo}: $fileName")
            }
        }
    }

    private fun parseCommand(root: Node, cwd: Node, line: String): CommandResult {
        val splits = line.split(" ")
        val command = splits[1]
        var newCwd = cwd

        if (command == "ls") {
            return CommandResult(newCwd, Command.Ls)
        }

        if (command != "cd") {
            throw Error("Unexpectd command $command")
        }

        val directory = splits[2]
        if (directory == "/")
            newCwd = root
        else if (directory == "..")
            newCwd = newCwd.parent ?: newCwd
        else {
            val child = newCwd.tryGetChild(directory)
            if (child == null) {
                newCwd.addChild(Node(directory, newCwd))
            }

            newCwd = newCwd.tryGetChild(directory)!!
        }

        return CommandResult(newCwd, Command.Ls)
    }
}