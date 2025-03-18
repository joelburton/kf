package kf

class FScanner(val vm: ForthVM, val bufStartAddr: Int, val bufEndAddr: Int) {
    var bufPtr = 0
    var bufLen = 0

    companion object {
        const val SPACE = 0x20
    }

    /** Reset */

    fun reset() {
        bufPtr = bufStartAddr
        bufLen = 0
    }

    /** Set string to the input buffer. */

    fun fill(s: String) {
        bufLen = 0
        for (c in s) vm.mem[bufStartAddr + bufLen++] = c.code
        bufPtr = bufStartAddr
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

    fun parseName(): Pair<Int, Int> {
        val max = bufStartAddr + bufLen
        var at = 0
        var len = 0

        while (bufPtr < max && vm.mem[bufPtr] == SPACE) bufPtr += 1
        at = bufPtr
        while (bufPtr < max && vm.mem[bufPtr] != SPACE) bufPtr += 1
        len = bufPtr - at
        if (bufPtr < max && vm.mem[bufPtr++] != SPACE)
            throw Exception("WOULD NEVER HAPPEN?")
        return Pair(at, len)
    }

    /** General parse for an ending character. Returns (addr, len) */

    fun parse(term: Char): Pair<Int, Int> {
        val max = bufStartAddr + bufLen
        var at = bufPtr
        var phraseLen = 0

        while (bufPtr < max && vm.mem[bufPtr] != term.code) bufPtr += 1
        if (bufPtr == max) throw ParseError("Expected terminator")
        phraseLen = bufPtr - at
        bufPtr += 1  // advance past closing term
        if (bufPtr < max) if (vm.mem[bufPtr++] != SPACE)
            throw ParseError("Unexpected character")
        return Pair(at, phraseLen)
    }

    /** Get rest of line. */

    fun nextLine() {
        bufPtr = bufEndAddr
        bufLen = bufEndAddr - bufStartAddr
    }

    val atEnd get() = bufPtr == bufStartAddr + bufLen
}
