class Day3: Day(3) {
    override fun solve1(isExample: Boolean): String {
        var sumOfPrios = 0

        for (line in lines) {
            val left = line.slice(IntRange(0, (line.length / 2) - 1))
            val right = line.slice(IntRange(line.length / 2, line.length - 1))

            val leftSet = left.toSet()
            val rightSet = right.toSet()
            val intersect = leftSet.intersect(rightSet).toList()

            if (intersect.size > 1) {
                throw Error("There should only be one match")
            }

            val match = intersect[0]
            sumOfPrios += charToPriority(match)
        }
        return sumOfPrios.toString()
    }

    override fun solve2(isExample: Boolean): String {
        var i = 0
        var sum = 0

        while (i < lines.size) {
            val group = listOf(lines[i].toSet(), lines[i + 1].toSet(), lines[i + 2].toSet())
            val intersect = group.reduce { set, acc ->
                acc.intersect(set)
            }.toList()

            println("[3.2] ${lines[i]} ${lines[i + 1]} ${lines[i + 2]} $intersect")
            sum += charToPriority(intersect[0])

            i += 3
        }

        return sum.toString()
    }

    override fun getExampleAnswer1(): String {
        TODO("Not yet implemented")
    }

    override fun getExampleAnswer2(): String {
        TODO("Not yet implemented")
    }


    private fun charToPriority(letter: Char): Int {
        return if (letter.isLowerCase()) {
            letter.code - 'a'.code + 1
        }
        else {
            letter.code - 'A'.code + 27
        }
    }
}