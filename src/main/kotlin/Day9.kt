import java.awt.Point
import kotlin.math.abs
import kotlin.math.sign

enum class Direction {
    Unknown,
    Up,
    Left,
    Down,
    Right
}
class Day9: Day(9) {
    override fun solve() {
        solve1()
        solve2()
    }

    private fun solve1() {
        println("[9.1] Seen: ${simulateKnots(2)}")
    }

    private fun solve2() {
        println("[9.2] Seen: ${simulateKnots(10)}")
    }

    private fun simulateKnots(knotsAmount: Int): Int {
        val grid = mutableListOf(mutableListOf(true))
        val knots = MutableList<Point>(knotsAmount) { Point() }

        for (line in lines) {
            val splits = line.split(" ")
            if (splits.size != 2) {
                throw Error("Invalid line $line")
            }

            val direction = splits[0]
            val steps = splits[1].toInt()

            move(grid, knots, direction, steps)
        }

        val totalSeen = grid.sumOf { row ->
            row.filter { it }.size
        }

        return totalSeen
    }

    private fun move(grid: MutableList<MutableList<Boolean>>, knots: List<Point>, directionString: String, steps: Int) {
        val direction = toDirection(directionString)

        for (step in steps downTo (1)) {
            applyMove(knots[0], direction)
            fixMove(grid, knots)
            for (i in 0 until knots.size - 1) {
                fixTail(knots[i], knots[i + 1])
            }

            val tail = knots.last()
            grid[tail.y][tail.x] = true

            // printGrid(grid, knots)
        }
    }

    private fun printGrid(grid: MutableList<MutableList<Boolean>>, knots: List<Point>) {
        grid.reversed().forEachIndexed { reversedRowIndex, row ->
            val rowIndex = grid.size - 1 - reversedRowIndex

            row.forEachIndexed { columnIndex, value ->
                val knotIndex = knots.indexOfFirst {
                    it.x == columnIndex && it.y == rowIndex
                }

                if (knotIndex == 0) {
                    print("H")
                }
                else if (knotIndex > 0) {
                    print("$knotIndex")
                }
                else {
                    print(if (value) "#" else ".")
                }
            }
            println()
        }
        println()
    }

    private fun extendGrid(grid: MutableList<MutableList<Boolean>>, direction: Direction) {
        val currentHeight = grid.size
        val currentWidth = grid[0].size

        when (direction) {
            Direction.Up -> {
                grid.add(MutableList(currentWidth) { false })
            }
            Direction.Down -> {
                grid.add(0, MutableList(currentWidth) { false })
            }
            Direction.Left -> {
                for (row in grid) {
                    row.add(0, false)
                }
            }
            Direction.Right -> {
                for (row in grid) {
                    row.add(false)
                }
            }
            else -> {

            }
        }
    }

    private fun toDirection(letter: String): Direction {
        return when(letter) {
            "L" -> Direction.Left
            "U" -> Direction.Up
            "R" -> Direction.Right
            "D" -> Direction.Down
            else -> Direction.Unknown
        }
    }

    private fun applyMove(point: Point, direction: Direction) {
        when (direction) {
            Direction.Up -> point.y += 1
            Direction.Down -> point.y -= 1
            Direction.Left -> point.x -= 1
            Direction.Right -> point.x += 1
            Direction.Unknown -> {}
        }
    }

    private fun fixMove(grid: MutableList<MutableList<Boolean>>, knots: List<Point>) {
        val head = knots[0]

        if (head.x < 0) {
            extendGrid(grid, Direction.Left)
            knots.forEach { it.x += 1 }
        }
        else if (head.x == grid[0].size) {
            extendGrid(grid, Direction.Right)
        }

        if (head.y < 0) {
            extendGrid(grid, Direction.Down)
            knots.forEach { it.y += 1 }
        }
        else if (head.y == grid.size) {
            extendGrid(grid, Direction.Up)
        }
    }

    private fun fixTail(head: Point, tail: Point) {
        val delta = head.minus(tail)

        if (abs(delta.x) == 2 && abs(delta.y) >= 1) {
            tail.x += 1 * delta.x.sign
            tail.y += 1 * delta.y.sign
        }
        else if (abs(delta.x) == 2 && abs(delta.y) == 0) {
            tail.x += 1 * delta.x.sign
        }
        else if (abs(delta.y) == 2 && abs(delta.x) >= 1) {
            tail.x += 1 * delta.x.sign
            tail.y += 1 * delta.y.sign
        }
        else if (abs(delta.y) == 2 && abs(delta.x) == 0) {
            tail.y += 1 * delta.y.sign
        }
        else if (abs(delta.x) > 1 || abs(delta.y) > 1) {
            throw Error("Can't fix $delta")
        }
    }
}

fun Point.minus(other: Point): Point {
    return Point(x - other.x, y - other.y)
}