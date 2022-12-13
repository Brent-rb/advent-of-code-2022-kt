import kotlin.math.max
import kotlin.math.min

class Day10: Day(10) {
    private val cycles = mutableListOf<Int>(1)
    override fun solve() {
        constructCycles()
        println(cycles)
        solve1()
        solve2()
    }

    private fun solve1() {
        println("[10.1] Strength: ${getStrength(220)}")
    }

    private fun solve2() {
        val crtLines = constructCrt()
        drawCrt(crtLines)
    }

    private fun constructCycles() {
        for (line in lines) {
            cycles.addAll(handleLine(line, cycles.last()))
        }
    }

    private fun drawCrt(crtLines: List<List<Boolean>>) {
        crtLines.forEach { line ->
            line.forEach { value ->
                print(if (value) "#" else ".")
            }
            println()
        }
    }

    private fun constructCrt(): List<List<Boolean>> {
        val crtChunks = cycles.subList(0, 240).chunked(40)
        val crtLines = MutableList(crtChunks.size) { MutableList(40) { false } }

        crtChunks.forEachIndexed { index, line ->
            for (crtCycle in 0 until 40) {
                val currentPosition = line[crtCycle]

                if (crtCycle >= currentPosition - 1 && crtCycle <= currentPosition + 1) {
                    crtLines[index][crtCycle] = true
                }
            }
        }

        return crtLines
    }
    private fun getStrength(max: Int, chunkSize: Int = 40, startChunkSize: Int = 20): Int {
        val chunkStride = (startChunkSize + chunkSize)

        val firstChunk = cycles.subList(0, startChunkSize)
        val firstStrength = firstChunk.last() * startChunkSize

        val restChunks = cycles.subList(startChunkSize, min(cycles.size, max)).chunked(chunkSize)
        val restStrengths = restChunks.mapIndexed { index, list ->
            list.last() * ((index * chunkSize) + chunkStride)
        }

        return firstStrength + restStrengths.sum()
    }

    private fun handleLine(line: String, register: Int): List<Int> {
        if (line == "noop") {
           return listOf(register)
        }

        val splits = line.split(" ")
        if (splits.size != 2) {
            throw Error("Invalid line $line")
        }

        val instruction = splits[0]
        val param = splits[1].toInt()

        if (instruction == "addx") {
            return listOf(register, register + param)
        }
        else {
            throw Error("Unsupported instruction: $instruction")
        }
    }
}