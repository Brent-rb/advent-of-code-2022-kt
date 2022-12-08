import java.io.File

abstract class Day(number: Int) {
    protected var lines: List<String>

    init {
        lines = File("inputs/day${number}.txt").readLines()
    }

    abstract fun solve()
}