
fun IntRange.contains(other: IntRange): Boolean {
    return other.first >= first && other.last <= last
}

fun IntRange.overlaps(other: IntRange): Boolean {
    return other.last in first..last ||
            other.first in first .. last ||
            first in other.first .. other.last ||
            last in other.first .. other.last
}

class Day4: Day(4) {
    override fun solve1(isExample: Boolean): String {
        val containingPairsCount = lines.map { toNumberPair(it) }.filter { pairContains(it) }.size
        return "$containingPairsCount"
    }

    override fun solve2(isExample: Boolean): String {
        val overlappingPairsCount = lines.map { toNumberPair(it) }.filter { pairOverlaps(it) }.size
        return "$overlappingPairsCount"
    }

    override fun getExampleAnswer1(): String {
        return "2"
    }

    override fun getExampleAnswer2(): String {
        return "4"
    }

    private fun toNumberPair(line: String): Pair<IntRange, IntRange> {
        val splits = line.split(",")
        if (splits.size != 2) {
            throw Error("Invalid line $line")
        }

        return Pair(toIntRange(splits[0]), toIntRange(splits[1]))
    }

    private fun toIntRange(line: String): IntRange {
        val splits = line.split("-")
        if (splits.size != 2) {
            throw Error("Invalid range $line")
        }

        val start = splits[0].toInt()
        val end = splits[1].toInt()

        return IntRange(start, end)
    }

    private fun pairContains(pair: Pair<IntRange, IntRange>): Boolean {
        return pair.first.contains(pair.second) || pair.second.contains(pair.first)
    }

    private fun pairOverlaps(pair: Pair<IntRange, IntRange>): Boolean {
        return pair.first.overlaps(pair.second)
    }
}