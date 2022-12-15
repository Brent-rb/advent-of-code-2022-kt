import java.awt.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class VisibleResult(val visible: Int, val open: Int, val freeSpots: List<Int>)

fun Point.taxicabDistance(point: Point): Int {
    return abs(x - point.x) + abs(y - point.y)
}
class Day15: Day(15) {
    private val sensorPairs = mutableListOf<Pair<Point, Point>>()

    override fun prepare1() {
        sensorPairs.clear()
        parse()
    }

    override fun solve1(isExample: Boolean): String {
        return "${getVisibleOn(if (isExample) 10 else 2000000).visible}"
    }

    override fun solve2(isExample: Boolean): String {
        val min = if (isExample) 20 else 4000000

        var startTime = System.currentTimeMillis()
        val point = getNothings(0, min)
        var endTime = System.currentTimeMillis()
        println("[15.2.1] Took ${endTime - startTime}ms")

        startTime = System.currentTimeMillis()
        val point2 = getNothing2(0, min)
        endTime = System.currentTimeMillis()
        println("[15.2.2] Took ${endTime - startTime}ms")

        return (point.x.toLong() * 4000000L + point.y).toString()
    }

    override fun getExampleAnswer1(): String {
        return "26"
    }

    override fun getExampleAnswer2(): String {
        return "56000011"
    }

    private fun getNothing2(lowerBound: Int, upperBound: Int): Point {
        for (y in lowerBound until upperBound) {
            val result = getVisibleOn(y, lowerBound, upperBound)

            if (result.open == 1) {
                return Point(result.freeSpots[0], y)
            }
        }

        return Point()
    }
    private fun getVisibleOn(rowNumber: Int, lowerBound: Int = -100000000, upperBound: Int = 100000000): VisibleResult {
        val visibles = mutableListOf<Pair<Int, Int>>()
        var minX = upperBound
        var maxX = lowerBound

        for (pair in sensorPairs) {
            val sensor = pair.first
            val beacon = pair.second
            val distance = sensor.taxicabDistance(beacon)

            val yDistance = abs(sensor.y - rowNumber)
            val lineWidth = (2 * distance + 1) - (2 * yDistance)
            if (lineWidth < 0) {
                continue
            }

            var lineMinX = sensor.x - lineWidth / 2
            var lineMaxX = sensor.x + lineWidth / 2

            lineMinX = max(lineMinX, lowerBound)
            lineMaxX = min(lineMaxX, upperBound)

            if (lineMinX < minX)
                minX = lineMinX
            if (lineMaxX > maxX) {
                maxX = lineMaxX
            }

            visibles.add(Pair(lineMinX, lineMaxX))
        }

        var freeSpots = mutableListOf<Int>()
        var x = minX
        var visible = 0
        var open = 0
        while (x <= maxX) {
            var foundMatchingPair = false

            for (pair in visibles) {
                val pairMinX = pair.first
                val pairMaxX = pair.second

                if (x in pairMinX .. pairMaxX) {
                    visible += pairMaxX - x + 1
                    x = pairMaxX + 1
                    foundMatchingPair = true
                }
            }

            if (x <= maxX && !foundMatchingPair) {
                freeSpots.add(x)
                x++
                open++
            }
        }

        return VisibleResult(visible - 1, open, freeSpots)
    }

    private fun getNothings(lowerBound: Int, upperBound: Int): Point {
        for (y in lowerBound until upperBound) {
            var x = 0
            while (x < upperBound) {
                var sensorInRange = false

                for (pair in sensorPairs) {
                    val sensor = pair.first
                    val beacon = pair.second
                    val distance = sensor.taxicabDistance(beacon)

                    val yDistance = abs(sensor.y - y)
                    val lineWidth = (2 * distance + 1) - (2 * yDistance)
                    if (lineWidth < 0) {
                        continue
                    }
                    val lineMinX = sensor.x - lineWidth / 2
                    val lineMaxX = sensor.x + lineWidth / 2

                    if (x in (lineMinX..lineMaxX)) {
                        sensorInRange = true
                        x = lineMaxX
                        break
                    }
                }

                if (!sensorInRange) {
                    return Point(x, y)
                }

                x++
            }
        }

        throw Error("No point found.")
    }

    private fun parse() {
        val regex = """.+?x=(-?\d+).+?y=(=?\d+).+?x=(-?\d+).+?y=(-?\d+)""".toRegex()

        for (line in lines) {
            val match = regex.find(line)
            if (match == null || match.groupValues.size != 5) {
                throw Error("Invalid input $line")
            }

            val sensorX = match.groupValues[1].toInt()
            val sensorY = match.groupValues[2].toInt()
            val beaconX = match.groupValues[3].toInt()
            val beaconY = match.groupValues[4].toInt()
            val sensor = Point(sensorX, sensorY)
            val beacon = Point(beaconX, beaconY)

            sensorPairs.add(Pair(sensor, beacon))
        }
    }
}