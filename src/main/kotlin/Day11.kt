import java.math.BigInteger

data class Monkey(
    val number: Int,
    val operation: (Long) -> Long,
    val test: (Long) -> Int,
    var inventory: MutableList<Long>
)

class Day11: Day(11) {
    private val monkeys: MutableList<Monkey> = mutableListOf()
    private var commonDivisor = 1L
    override fun solve() {
        constructMonkeys()
        solve1()

        monkeys.clear()
        commonDivisor = 1L
        constructMonkeys()
        solve2()
    }

    private fun solve1() {
        println("[11.1] Monkey Madness: ${simulateRounds(20, true)}")
    }

    private fun solve2() {
        println("[11.2] Monkey Madness: ${simulateRounds(10000, false)}")
    }

    private fun simulateRounds(rounds: Int, divideBy3: Boolean): Long {
        val inspectCounts = MutableList(monkeys.size) { 0L }

        for (i in 0 until rounds) {
            val newInventories = MutableList<MutableList<Long>>(monkeys.size) { mutableListOf() }

            monkeys.forEachIndexed { monkeyIndex, monkey ->
                for (item in monkey.inventory) {
                    inspectCounts[monkey.number]++

                    var value = monkey.operation(item)
                    if (divideBy3) {
                        value /= 3
                    }
                    else {
                        value %= commonDivisor
                    }

                    val newMonkey = monkey.test(value)
                    if (newMonkey > monkeyIndex) {
                        monkeys[newMonkey].inventory.add(value)
                    }
                    else {
                        newInventories[newMonkey].add(value)
                    }
                }
            }

            for (monkeyIndex in 0 until monkeys.size) {
                monkeys[monkeyIndex].inventory = newInventories[monkeyIndex]
            }
        }

        inspectCounts.sortByDescending { it }
        return inspectCounts[0] * inspectCounts[1]
    }

    private fun constructMonkeys() {
        val monkeyLines = lines.filter { it.isNotBlank() }.chunked(6)

        for (monkeyData in monkeyLines) {
            monkeys.add(createMonkey(monkeyData))
        }
    }

    private fun createMonkey(monkeyData: List<String>): Monkey {
        if (monkeyData.size != 6) {
            throw Error("Invalid monkey data")
        }

        val monkeyNumber = monkeyData[0]
        val monkeyInventory = monkeyData[1]
        val monkeyOperation = monkeyData[2]
        val monkeyTest = monkeyData[3]
        val monkeyTrue = monkeyData[4]
        val monkeyFalse = monkeyData[5]

        val number = extractNumber(monkeyNumber)
        val inventory = extractInventory(monkeyInventory)
        val operation = extractOperation(monkeyOperation)
        val test = extractTest(monkeyTest, monkeyTrue, monkeyFalse)

        return Monkey(number, operation, test, inventory.toMutableList())
    }

    private fun extractNumber(monkeyNumber: String): Int {
        val regex = """Monkey (\d+):""".toRegex()
        val result = regex.find(monkeyNumber)

        return result?.groupValues?.get(1)?.toInt() ?: throw Error("Invalid Monkey")
    }

    private fun extractInventory(inventoryString: String): List<Long> {
        val regex = """Starting items: (.+)""".toRegex()
        val result = regex.find(inventoryString)

        return result?.groupValues?.get(1)?.split(",")?.map { it.trim().toLong() } ?: listOf()
    }

    private fun extractOperation(operationString: String): (Long) -> Long {
        val regex = """Operation: new = (.+?) (\*|\+) (.+)""".toRegex()
        val result = regex.find(operationString)

        val left = result?.groupValues?.get(1) ?: throw Error("Invalid Monkey")
        val operator = result?.groupValues?.get(2) ?: throw Error("Invalid Monkey")
        val right = result?.groupValues?.get(3) ?: throw Error("Invalid Monkey")

        return {
            var leftValue = it
            if (left != "old") {
                leftValue = left.toLong()
            }

            var rightValue = it
            if (right != "old") {
                rightValue = right.toLong()
            }

            if (operator == "*")
                leftValue * rightValue
            else if (operator == "+")
                leftValue + rightValue
            else
                throw Error("Unsupported Operation: $left $operator $right")
        }
    }

    private fun extractTest(testLine: String, trueLine: String, falseLine: String): (Long) -> Int {
        val trueRegex = """If true: throw to monkey (.+)""".toRegex()
        val falseRegex = """If false: throw to monkey (.+)""".toRegex()
        val testRegex = """Test: divisible by (.+)""".toRegex()

        val trueResult = trueRegex.find(trueLine)
        val falseResult = falseRegex.find(falseLine)
        val testResult = testRegex.find(testLine)

        val trueNumber = trueResult?.groupValues?.get(1)?.toInt() ?: throw Error("Invalid Monkey")
        val falseNumber = falseResult?.groupValues?.get(1)?.toInt() ?: throw Error("Invalid Monkey")
        val divisibleNumber = testResult?.groupValues?.get(1)?.toLong() ?: throw Error("Invalid Monkey")

        commonDivisor *= divisibleNumber

        return {
            if (it % divisibleNumber == 0L) {
                trueNumber
            }
            else {
                falseNumber
            }
        }
    }
}