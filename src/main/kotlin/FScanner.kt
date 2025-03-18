package kf

class FScanner(val vm: ForthVM, val bufStartAddr: Int, val bufEndAddr: Int) {
    var bufPtr = 0  // buffer pointer
    var bufLen = 0  // length of entire buffer
    var tokStart = 0
    var tokLen = 0

    companion object {
        const val SPACE = 0x20
    }

    /** Reset */

    fun reset() {
        bufPtr = bufStartAddr
        bufLen = 0
        tokStart = bufStartAddr
        tokLen = 0
    }

    /** Set string to the input buffer. */

    fun fill(s: String) {
        reset()
        for (c in s) vm.mem[bufStartAddr + bufLen++] = c.code
        bufPtr = bufStartAddr
    }

    // Perhaps just for debugging
    override fun toString(): String {
        val chars = CharArray(bufLen) { i -> vm.mem[bufStartAddr + i].toChar() }
        return chars.concatToString()
    }

    /** Get range of buffer as normal K string. */

    fun getAsString(addr: Int, len: Int): String {
        val chars = CharArray(len) { i -> vm.mem[addr + i].toChar() }
        return chars.concatToString()
    }

    /** Get range of buffer as normal K string. */

    fun getAsCString(addr: Int): String {
        val len = vm.mem[addr]
        val chars = CharArray(len) { i -> vm.mem[addr + i +1].toChar() }
        return chars.concatToString()
    }

    /** Get space-separated word. Returns (addr, len). */

    fun parseName() {
        val max = bufStartAddr + bufLen

        while (bufPtr < max && vm.mem[bufPtr] == SPACE) bufPtr += 1
        tokStart = bufPtr
        while (bufPtr < max && vm.mem[bufPtr] != SPACE) bufPtr += 1
        tokLen = bufPtr - tokStart
        if (bufPtr < max && vm.mem[bufPtr++] != SPACE)
            throw Exception("WOULD NEVER HAPPEN?")
    }

    fun parseNameToStr(): String {
        parseName()
        val chars = CharArray(tokLen) { i -> vm.mem[tokStart + i].toChar() }
        return chars.concatToString()
    }

    fun parseNameToPAir(): Pair<Int, Int> {
        parseName()
        return Pair(tokStart, tokLen)
    }

    fun parseToStr(term: Char): String {
        parse(term)
        val chars = CharArray(tokLen) { i -> vm.mem[tokStart + i].toChar() }
        return chars.concatToString()
    }

    fun parseToPair(term: Char): Pair<Int, Int> {
        parse(term)
        return Pair(tokStart, tokLen)
    }

    /** General parse for an ending character. Returns (addr, len) */

    fun parse(term: Char) {
        val max = bufStartAddr + bufLen
        var at = bufPtr
        var phraseLen = 0

        tokStart = bufPtr
        while (bufPtr < max && vm.mem[bufPtr] != term.code) bufPtr += 1
        if (bufPtr == max) throw ParseError("Expected terminator")
        tokLen = bufPtr - tokStart
        bufPtr += 1  // advance past closing term
        if (bufPtr < max) if (vm.mem[bufPtr++] != SPACE)
            throw ParseError("Unexpected character")
    }

    /** Get rest of line. */

    fun nextLine() {
        bufPtr = bufEndAddr
//        bufLen = bufEndAddr - bufStartAddr
    }

//    val atEnd get() = bufPtr == bufStartAddr + bufLen
}
