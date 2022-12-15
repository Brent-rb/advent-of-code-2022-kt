import java.util.Stack

class Day5: Day(5) {
    override fun solve1(isExample: Boolean): String {
        val stacks = constructStacks()

        for (line in lines) {
            if (!line.startsWith("move")) {
                continue
            }

            applyMove(stacks, line)
        }

        return stacks.joinToString("") {
            it.peek().replace("[", "").replace("]", "")
        }
    }

    override fun solve2(isExample: Boolean): String {
        val stacks = constructStacks()

        for (line in lines) {
            if (!line.startsWith("move")) {
                continue
            }

            applyMove2(stacks, line)
        }

        return stacks.joinToString("") {
            it.peek().replace("[", "").replace("]", "")
        }
    }

    override fun getExampleAnswer1(): String {
        return "CMZ"
    }

    override fun getExampleAnswer2(): String {
        return "MCD"
    }

    private fun applyMove(stacks: MutableList<Stack<String>>, move: String) {
        val regex = """move (\d+) from (\d+) to (\d+)""".toRegex()
        val matchResults = regex.find(move)

        val amount = matchResults?.groupValues?.get(1)?.toInt() ?: 0
        val from = matchResults?.groupValues?.get(2)?.toInt() ?: 0
        val to = matchResults?.groupValues?.get(3)?.toInt() ?: 0

        if (amount == 0) {
            throw Error("Invalid move $move")
        }

        // println("[5.1] Move $amount from $from to $to")
        for (i in 0 until amount) {
            stacks[to - 1].add(stacks[from - 1].pop())
        }
    }

    private fun applyMove2(stacks: MutableList<Stack<String>>, move: String) {
        val regex = """move (\d+) from (\d+) to (\d+)""".toRegex()
        val matchResults = regex.find(move)

        val amount = matchResults?.groupValues?.get(1)?.toInt() ?: 0
        val from = matchResults?.groupValues?.get(2)?.toInt() ?: 0
        val to = matchResults?.groupValues?.get(3)?.toInt() ?: 0

        if (amount == 0) {
            throw Error("Invalid move $move")
        }

        // println("[5.2] Move $amount from $from to $to")
        val poppedValues = mutableListOf<String>()
        for (i in 0 until amount) {
            poppedValues.add(stacks[from - 1].pop())
        }

        poppedValues.reversed().forEach {
            stacks[to - 1].add(it)
        }
    }

    private fun constructStacks(): MutableList<Stack<String>> {
        val stackLists = mutableListOf<MutableList<String>>()
        val stacks = mutableListOf<Stack<String>>()

        for (line in lines) {
            if (line.contains("[")) {
                val stack = line.chunked(4).map { it.trim() }

                while (stackLists.size < stack.size) {
                    stackLists.add(mutableListOf())
                }
                stack.forEachIndexed { index, s ->
                    if (s.isBlank())
                        return@forEachIndexed

                    stackLists[index].add(s)
                }
            }
            else if (line.isBlank()) {
                for (stackList in stackLists) {
                    val stack = Stack<String>()
                    stackList.reversed().forEach {
                        stack.add(it)
                    }

                    stacks.add(stack)
                }

                return stacks
            }
        }

        return stacks
    }

    private fun printStack(stacks: Collection<List<String>>) {
        var done = false
        var height = 0
        val maxIndex = stacks.maxOf { it.size } - 1

        while (!done) {
            var allSkipped = true
            var index = maxIndex - height

            for (stack in stacks) {
                if (index < stack.size && index >= 0) {
                    allSkipped = false
                    print(stack[index])
                }
                else {
                    print("   ")
                }
            }
            println()
            height += 1;
            done = allSkipped
        }
    }
}