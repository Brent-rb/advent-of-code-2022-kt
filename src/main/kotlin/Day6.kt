class Day6: Day(6) {
    override fun solve() {
        solve1()
        solve2()
    }

    fun solve1() {
        val index = searchForDistinctSubList(4)
        println("[6.1] $index")
    }

    fun solve2() {
        val index = searchForDistinctSubList(14)
        println("[6.1] $index")
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