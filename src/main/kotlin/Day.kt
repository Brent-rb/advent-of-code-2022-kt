import java.io.File

abstract class Day(private val dayNumber: Int) {
    protected lateinit var lines: List<String>

    fun solve() {
        lines = File("inputs/day${dayNumber}_example.txt").readLines()

        val exampleAnswer1 = getExampleAnswer1()
        prepare1()
        val exampleResult1 = solve1(true)
        if (exampleAnswer1 != exampleResult1) {
            throw Error("Validation Error, expected example 1 answer $exampleAnswer1, got $exampleResult1")
        }
        else {
            println("[15.1e] Passed")
        }

        val exampleAnswer2 = getExampleAnswer2()
        prepare2()
        val exampleResult2 = solve2(true)
        if (exampleAnswer2 != exampleResult2) {
            throw Error("Validation Error, expected example 2 answer $exampleAnswer2, got $exampleResult2")
        }
        else {
            println("[15.2e] Passed")
        }

        lines = File("inputs/day${dayNumber}.txt").readLines()
        prepare1()
        val result1 = solve1(false)
        println("[$dayNumber.1] Answer: $result1")

        prepare2()
        val result2 = solve2(false)
        println("[$dayNumber.1] Answer: $result2")
    }

    open fun prepare1() {

    }

    open fun prepare2() {

    }

    abstract fun solve1(isExample: Boolean): String
    abstract fun solve2(isExample: Boolean): String
    abstract fun getExampleAnswer1(): String
    abstract fun getExampleAnswer2(): String
}