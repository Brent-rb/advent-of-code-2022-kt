import java.awt.Point

data class DijkstraPoint(
    val point: Point,
    val height: Int,
    var distance: Int,
    var previousPoint: Point = Point(-1, -1)
)
class Day12: Day(12) {
    private lateinit var heightMap: List<MutableList<Int>>
    private val startPos = Point()
    private val endPos = Point()

    override fun prepare1() {
        constructHeightMap()
    }

    override fun solve1(isExample: Boolean): String {
        return "${dijkstra(startPos).distance}"
    }

    override fun solve2(isExample: Boolean): String {
        return "${dijkstra(this.startPos, 0).distance}"
    }

    override fun getExampleAnswer1(): String {
        TODO("Not yet implemented")
    }

    override fun getExampleAnswer2(): String {
        TODO("Not yet implemented")
    }
    private fun dijkstra(startPos: Point, aWeight: Int = 1): DijkstraPoint {
        val points = mutableListOf<DijkstraPoint>()
        val dijkstraGrid = MutableList<MutableList<DijkstraPoint>>(heightMap.size) { mutableListOf() }

        heightMap.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, height ->
                val dijkstraPoint = DijkstraPoint(Point(columnIndex, rowIndex), heightMap[rowIndex][columnIndex], 10000000)
                points.add(dijkstraPoint)
                dijkstraGrid[rowIndex].add(dijkstraPoint)
            }
        }
        dijkstraGrid[startPos.y][startPos.x].distance = 0
        points.sortBy { it.distance }

        while (points.isNotEmpty()) {
            val closest = points.removeFirst()

            val neighbours = listOf(
                Point(closest.point.x + 1, closest.point.y),
                Point(closest.point.x - 1, closest.point.y),
                Point(closest.point.x, closest.point.y + 1),
                Point(closest.point.x, closest.point.y - 1)
            ).filter {
                !(it.x < 0 || it.y < 0 || it.x >= heightMap[0].size || it.y >= heightMap.size)
            }.map {
                dijkstraGrid[it.y][it.x]
            }

            for (neighbour in neighbours) {
                val edge = if (neighbour.height == 0 && closest.height == 0)
                    aWeight
                else if (neighbour.height - 1 <= closest.height)
                    1
                else
                    10000000

                val distance = closest.distance + edge
                if (distance < neighbour.distance) {
                    neighbour.distance = distance
                    neighbour.previousPoint = closest.point
                }
            }

            points.sortBy { it.distance }
        }

        return dijkstraGrid[endPos.y][endPos.x]
    }

    private fun constructHeightMap() {
        val height = lines.size
        val width = lines[0].length

        heightMap = MutableList(height) { MutableList(width) { 0 } }

        lines.forEachIndexed { row, line ->
            line.forEachIndexed { column, heightChar ->
                var code = heightChar.code

                if (heightChar == 'S') {
                    startPos.x = column
                    startPos.y = row
                    code = 'a'.code
                }
                else if (heightChar == 'E') {
                    endPos.x = column
                    endPos.y = row
                    code = 'z'.code
                }

                heightMap[row][column] = code - 'a'.code
            }
        }
    }

    private fun printHeightMap() {
        for (row in heightMap) {
            for (column in row) {
                print(Char('a'.code + column))
            }

            println()
        }
    }
}