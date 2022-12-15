
class PacketEntry(
    val number: Int? = null,
    val list: List<PacketEntry>? = null
): Comparable<PacketEntry> {
    override fun toString(): String {
        if (number != null) {
            return number.toString()
        }
        else if (list != null) {
            return list.toString()
        }

        return ""
    }

    fun isNumber(): Boolean {
        return number != null
    }

    fun isList(): Boolean {
        return list != null
    }

    override fun compareTo(right: PacketEntry): Int {
        if (isNumber() && right.isNumber()) {
            return number!!.compareTo(right.number!!)
        }
        else if (isNumber() && right.isList()) {
            return PacketEntry(null, listOf(this)).compareTo(right)
        }
        else if (right.isNumber() && isList()) {
            return this.compareTo(PacketEntry(null, listOf(right)))
        }

        var i = 0
        val leftList = list!!
        val rightList = right.list!!
        while (i <  leftList.size && i < rightList.size) {
            val left = leftList[i]
            val right = rightList[i]
            val comparison = left.compareTo(right)
            if (comparison != 0) {
                return comparison
            }

            i++
        }

        return if (i == leftList.size && i == rightList.size) {
            0
        }
        else if (i == leftList.size) {
            -1
        }
        else {
            1
        }
    }
}

class Day13: Day(13) {
    private val entryPairs: MutableList<Pair<PacketEntry, PacketEntry>> = mutableListOf()

    override fun prepare1() {
        entryPairs.clear()
        constructPairs()
    }

    override fun solve1(isExample: Boolean): String {
        val rightIndices = mutableListOf<Int>()

        for (index in 0 until entryPairs.size) {
            val pair = entryPairs[index]

            if (pair.first <= pair.second) {
                // println("[13] Pair ${index + 1} is in order")
                rightIndices.add(index + 1)
            }
            else {
                // println("[13] Pair ${index + 1} is not in order")
            }
        }

        return "${rightIndices.sum()}"
    }

    override fun solve2(isExample: Boolean): String {
        val entries = getDecoderEntries()
        val index1 = entries.indexOfFirst {
            it.compareTo(PacketEntry(null, listOf(PacketEntry(null, listOf(PacketEntry(2)))))) == 0
        } + 1
        val index2 = entries.indexOfFirst {
            it.compareTo(PacketEntry(null, listOf(PacketEntry(null, listOf(PacketEntry(6)))))) == 0
        } + 1

        return "${index1 * index2}"
    }

    override fun getExampleAnswer1(): String {
        return "13"
    }

    override fun getExampleAnswer2(): String {
        return "140"
    }
    private fun getDecoderEntries(): List<PacketEntry> {
        var entries = entryPairs.flatMap {
            listOf(it.first, it.second)
        } + listOf(
            PacketEntry(null, listOf(PacketEntry(null, listOf(PacketEntry(2))))),
            PacketEntry(null, listOf(PacketEntry(null, listOf(PacketEntry(6)))))
        )

        return entries.sorted()
    }

    private fun constructPairs() {
        val pairs = lines.filter { it.isNotBlank() }.chunked(2)

        for (pair in pairs) {
            if (pair.size != 2) {
                throw Error("Invalid pair")
            }

            val left = pair[0]
            val right = pair[1]

            val leftEntry = parseList(left)
            val rightEntry = parseList(right)

            val leftValidation = leftEntry.toString().replace(" ", "")
            val rightValidation = rightEntry.toString().replace(" ", "")
            if (leftValidation != left || rightValidation != right) {
                println(left)
                println(leftValidation)

                println(right)
                println(rightValidation)
                throw Error("Validation error.")
            }

            entryPairs.add(Pair(leftEntry, rightEntry))
        }
    }

    private fun parseList(value: String): PacketEntry {
        val outputList = mutableListOf<PacketEntry>()
        var index = 1
        var digit = ""

        while (index < value.length) {
            val letter = value[index]

            if (letter == '[') {
                val end = searchEndOfList(value, index)
                outputList.add(parseList(value.substring(index, end + 1)))
                index = end + 1
            }
            else if (letter in '0'..'9') {
                digit += letter
            }
            else if ((letter == ',' || letter == ']') && digit.isNotEmpty()) {
                outputList.add(PacketEntry(digit.toInt()))
                digit = ""
            }

            index++
        }

        return PacketEntry(null, outputList)
    }

    private fun searchEndOfList(listString: String, startPosition: Int): Int {
        var depth = 1
        var endOfList = startPosition + 1

        while (endOfList < listString.length) {
            if (listString[endOfList] == '[') {
                depth += 1
            }
            else if (listString[endOfList] == ']') {
                depth -= 1
            }

            if (depth == 0) {
                break
            }

            endOfList++
        }

        if (endOfList == listString.length) {
            throw Error("Invalid list string: $listString")
        }

        return endOfList
    }
}