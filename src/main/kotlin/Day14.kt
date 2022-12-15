import java.awt.Point
import kotlin.math.max
import kotlin.math.min

enum class GridValue {
    Nothing,
    Sand,
    Rock,
    Void
}

enum class MoveResult {
    Blocked,
    Success,
    Void
}

fun Point.plus(x: Int, y: Int): Point {
    return Point(this.x + x, this.y + y)
}

fun Point.plus(point: Point): Point {
    return plus(point.x, point.y)
}

class VoidError: Error("Sand went into the void")

enum class GridDirection {
    Down,
    DownLeft,
    DownRight
}

data class DownResult(
    val point: Point,
    val hitVoid: Boolean
)

class Day14: Day(14) {
    private val instructions: MutableList<Pair<Point, Point>> = mutableListOf()
    private lateinit var grid: MutableList<MutableList<GridValue>>
    private val minPoint = Point(10000000, 0)
    private val maxPoint = Point(-10000000, -10000000)

    override fun prepare1() {
        instructions.clear()
        minPoint.x = 10000000
        minPoint.y = 0
        maxPoint.x = -10000000
        maxPoint.y = -10000000

        parseInput()
    }

    override fun solve1(isExample: Boolean): String {
        constructGrid()
        val sandCount = simulateSand()

        // println()
        // printGrid()
        return "$sandCount"
    }
    override fun solve2(isExample: Boolean): String {
        maxPoint.y += 2
        constructGrid()
        addFloor()

        val sandCount = simulateSand(true)
        // println()
        // printGrid()
        return "$sandCount"
    }

    override fun getExampleAnswer1(): String {
        return "24"
    }

    override fun getExampleAnswer2(): String {
        return "93"
    }

    private fun simulateSand(voidBlocks: Boolean = false): Int {
        val sandStart = Point(500, 0)
        var sandCount = 0
        var fallingOffEdge = false

        while (!fallingOffEdge) {
            var sandLocation = sandStart

            do {
                val iterationStartLocation = sandLocation
                val downResult = goDown(sandLocation)
                if (downResult.hitVoid && !voidBlocks) {
                    fallingOffEdge = true
                    break
                }
                sandLocation = downResult.point

                val downLeftResult = tryMove(sandLocation, GridDirection.DownLeft)
                if (downLeftResult == MoveResult.Success) {
                    sandLocation = sandLocation.plus(moveToPointDelta(GridDirection.DownLeft))
                    continue
                }
                else if (downLeftResult == MoveResult.Void && !voidBlocks) {
                    fallingOffEdge = true
                    break
                }
                else if (downLeftResult == MoveResult.Void) {
                    sandLocation = iterationStartLocation
                    extendLeft()
                    continue
                }

                val downRightResult = tryMove(sandLocation, GridDirection.DownRight)
                if (downRightResult == MoveResult.Success) {
                    sandLocation = sandLocation.plus(moveToPointDelta(GridDirection.DownRight))
                    continue
                }
                else if (downRightResult == MoveResult.Void && !voidBlocks) {
                    fallingOffEdge = true
                    break
                }
                else if (downRightResult == MoveResult.Void) {
                    sandLocation = iterationStartLocation
                    extendRight()
                    continue
                }

                setValue(sandLocation, GridValue.Sand)
                sandCount++
                break
            } while (true)

            if (sandLocation == sandStart) {
                break
            }
        }

        return sandCount
    }

    private fun goDown(startPoint: Point): DownResult {
        var wentDown = false
        var point = startPoint

        do {
            wentDown = false

            val moveResult = tryMove(point, GridDirection.Down)
            if (moveResult == MoveResult.Success) {
                point = point.plus(0, 1)
                wentDown = true
            }
            else if (moveResult == MoveResult.Void) {
                return DownResult(point, true)
            }
        } while (wentDown)

        return DownResult(point, false)
    }

    private fun tryMove(point: Point, direction: GridDirection): MoveResult {
        val newPoint = point.plus(moveToPointDelta(direction))

        return when (getValue(newPoint)) {
            GridValue.Void -> MoveResult.Void
            GridValue.Nothing -> MoveResult.Success
            else -> MoveResult.Blocked
        }
    }

    private fun extendLeft() {
        grid.forEachIndexed { index, row ->
            if (index == grid.size - 1) {
                row.add(0, GridValue.Rock)
            }
            else {
                row.add(0, GridValue.Nothing)
            }
        }

        minPoint.x--
    }

    private fun extendRight() {
        grid.forEachIndexed { index, row ->
            if (index == grid.size - 1) {
                row.add(GridValue.Rock)
            }
            else {
                row.add(GridValue.Nothing)
            }
        }

        maxPoint.x++
    }

    private fun moveToPointDelta(direction: GridDirection): Point {
        return when (direction) {
            GridDirection.Down -> Point(0, 1)
            GridDirection.DownLeft -> Point(-1, 1)
            GridDirection.DownRight -> Point(1, 1)
        }
    }

    private fun getValue(point: Point): GridValue {
        return if (point.x < minPoint.x || point.x > maxPoint.x || point.y < 0 || point.y > maxPoint.y) {
            GridValue.Void
        } else {
            grid[point.y - minPoint.y][point.x - minPoint.x]
        }
    }

    private fun setValue(point: Point, value: GridValue) {
        if (point.x < minPoint.x || point.x > maxPoint.x || point.y < 0 || point.y > maxPoint.y) {
            throw ArrayIndexOutOfBoundsException()
        }

        grid[point.y - minPoint.y][point.x - minPoint.x] = value
    }
    private fun constructGrid(extraHeight: Int = 0) {
        grid = MutableList((maxPoint.y - minPoint.y) + 1) {
            MutableList((maxPoint.x - minPoint.x) + 1) { GridValue.Nothing }
        }

        for (instruction in instructions) {
            val from = instruction.first
            val to = instruction.second

            val minY = min(from.y, to.y)
            val maxY = max(from.y, to.y)
            val minX = min(from.x, to.x)
            val maxX = max(from.x, to.x)

            for (row in minY .. maxY) {
                for (column in minX .. maxX) {
                    // setValue(grid, Point(column, row), GridValue.Rock)
                    grid[row - minPoint.y][column - minPoint.x] = GridValue.Rock
                }
            }
        }
    }

    private fun addFloor() {
        val row = grid[maxPoint.y - minPoint.y]

        for (i in 0 until row.size) {
            row[i] = GridValue.Rock
        }
    }

    private fun printGrid() {
        grid.forEach { row ->
            row.forEach { value ->
                when(value) {
                    GridValue.Nothing -> print(".")
                    GridValue.Sand -> print("o")
                    GridValue.Rock -> print("#")
                    GridValue.Void -> print("X")
                }
            }

            println()
        }
    }
    private fun parseInput() {
        for (line in lines) {
            val coords = line.split(" -> ")

            var previousPoint = toPoint(coords[0])
            updateMaxima(previousPoint)

            for (coord in coords.subList(1, coords.size)) {
                val currentPoint = toPoint(coord)
                updateMaxima(currentPoint)
                instructions.add(Pair(previousPoint, currentPoint))

                previousPoint = currentPoint
            }
        }
    }

    private fun printInstructions() {
        val texts = instructions.map {
            val from = it.first
            val to = it.second
            "[${from.x},${from.y} -> ${to.x},${to.y}]"
        }

        println(texts)
    }

    private fun toPoint(coords: String): Point {
        val splits = coords.split(",")
        if (splits.size != 2) {
            throw Error("Invalid coords $coords")
        }

        val x = splits[0].toInt()
        val y = splits[1].toInt()

        return Point(x, y)
    }

    private fun updateMaxima(point: Point) {
        if (point.x < minPoint.x)
            minPoint.x = point.x
        if (point.y < minPoint.y)
            minPoint.y = point.y

        if (point.x > maxPoint.x)
            maxPoint.x = point.x
        if (point.y > maxPoint.y)
            maxPoint.y = point.y
    }
}