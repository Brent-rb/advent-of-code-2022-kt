class Day6: Day(6) {
    override fun solve1(isExample: Boolean): String {
        val index = searchForDistinctSubList(4)
        return "$index"
    }

    override fun solve2(isExample: Boolean): String {
        val index = searchForDistinctSubList(14)
        return "$index"
    }

    override fun getExampleAnswer1(): String {
        TODO("Not yet implemented")
    }

    override fun getExampleAnswer2(): String {
        TODO("Not yet implemented")
    }

    fun searchForDistinctSubList(size: Int): Int {
        val characters = lines[0].toCharArray().toList()

        for (i in size until characters.size) {
            val set = setOf(*characters.subList(i - size, i).toTypedArray())
            if (set.size == size) {
                return i
            }
        }

        return 0
    }
}