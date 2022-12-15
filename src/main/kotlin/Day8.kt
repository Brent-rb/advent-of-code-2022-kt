class Day8: Day(8) {
    private var horizontalTrees: MutableList<MutableList<Int>> = mutableListOf()
    private var verticalTrees: MutableList<MutableList<Int>> = mutableListOf()
    private var visibleTrees: MutableList<MutableList<Boolean>> = mutableListOf()

    override fun prepare1() {
        readGrid()
    }

    override fun solve1(isExample: Boolean): String {
        var visibleAmount = 0

        horizontalTrees.forEachIndexed { row, trees ->
            val visibles = visibleTreesInList(trees)

            for (visibleIndex in visibles) {
                if (!visibleTrees[row][visibleIndex]) {
                    visibleAmount += 1
                    visibleTrees[row][visibleIndex] = true
                }
            }
        }

        verticalTrees.forEachIndexed { column, trees ->
            val visibles = visibleTreesInList(trees)

            for (visibleIndex in visibles) {
                if (!visibleTrees[visibleIndex][column]) {
                    visibleAmount += 1
                    visibleTrees[visibleIndex][column] = true
                }
            }
        }

        printVisibleTrees()

        return "$visibleAmount"
    }

    override fun solve2(isExample: Boolean): String {
        var scenicScore = 0

        for (row in 1 until horizontalTrees.size - 1) {
            val rowList = horizontalTrees[row]

            for (column in 1 until rowList.size - 1) {
                val tree = rowList[column]
                val columnList = verticalTrees[column]

                val up = columnList.subList(0, row).reversed()
                val down = columnList.subList(row + 1, columnList.size)
                val left = rowList.subList(0, column).reversed()
                val right = rowList.subList(column + 1, rowList.size)

                val tempScore = visibleFromLeft(tree, up) * visibleFromLeft(tree, down) * visibleFromLeft(tree, left) * visibleFromLeft(tree, right)
                if (tempScore > scenicScore) {
                    scenicScore = tempScore
                }
            }
        }

        return "$scenicScore"
    }

    override fun getExampleAnswer1(): String {
        TODO("Not yet implemented")
    }

    override fun getExampleAnswer2(): String {
        TODO("Not yet implemented")
    }

    private fun readGrid() {
        lines.forEachIndexed { row, line ->
            val treeLine = mutableListOf<Int>()
            val visibleLine = mutableListOf<Boolean>()

            line.forEachIndexed { index, it ->
                val tree = it.toString().toInt()
                treeLine.add(tree)
                visibleLine.add(false)
                if (row == 0) {
                    verticalTrees.add(mutableListOf())
                }
                verticalTrees[index].add(tree)
            }

            horizontalTrees.add(treeLine)
            visibleTrees.add(visibleLine)
        }
    }

    private fun visibleFromLeft(startTree: Int, treeLine: List<Int>): Int {
        var visibleFromLeft = 0

        for (tree in treeLine) {
            if (tree < startTree) {
                visibleFromLeft++
            }
            else {
                visibleFromLeft++
                break
            }
        }

        return visibleFromLeft
    }

    private fun visibleTreesInList(treeLine: List<Int>): List<Int> {
        var visibleFromLeft = 1
        var previousTree = treeLine[0]
        var visibles = mutableListOf<Int>(0, treeLine.size - 1)

        for (column in 1 until treeLine.size) {
            val tree = treeLine[column]
            if (tree > previousTree) {
                visibleFromLeft++
                visibles.add(column)
                previousTree = tree
            }
        }

        previousTree = treeLine[treeLine.size - 1]
        for (column in (treeLine.size - 2) downTo visibleFromLeft) {
            val tree = treeLine[column]
            if (tree > previousTree) {
                visibles.add(column)
                previousTree = tree
            }
        }

        return visibles
    }

    private fun printVisibleTrees() {
        for(visibleLine in visibleTrees) {
            for (tree in visibleLine) {
                print(if (tree) "X" else " ")
            }
            println()
        }
    }
}