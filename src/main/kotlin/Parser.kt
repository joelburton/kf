package kf

class Parser(val vm: ForthVM) {
    val bufAt = 0x10
    var bufPtr = 0
    var bufLen = 0

    companion object {
        const val SPACE = 0x20
    }

    /** Add string to the input buffer. */

    fun fill(s: String) {
        for (c in s) vm.mem[bufAt + bufLen++] = c.code
        bufPtr = bufAt
    }

    /** Get range of buffer as normal K string. */

    fun getAsString(addr: Int, len: Int): String {
        val chars = CharArray(len) { i -> vm.mem[addr + i].toChar() }
        return chars.concatToString()
    }

    /** Get space-separated word. Returns (addr, len). */

    fun parseName(): Pair<Int, Int> {
        val max = bufAt + bufLen
        var at = 0
        var len = 0

        while (bufPtr < max && vm.mem[bufPtr] == SPACE) bufPtr += 1
        at = bufPtr;
        while (bufPtr < max && vm.mem[bufPtr] != SPACE) bufPtr += 1
        len = bufPtr - at;
        if (bufPtr < max && vm.mem[bufPtr++] != SPACE)
            throw Exception("Unexpected char after term")
        return Pair(at, len);
    }

    /** General parse for an ending character. Returns (addr, len) */

    fun parse(term: Int): Pair<Int, Int> {
        val max = bufAt + bufLen
        var at = bufPtr
        var phraseLen = 0

        while (bufPtr < max && vm.mem[bufPtr] != term) bufPtr += 1
        if (bufPtr == max) throw Exception("Unexpected end of string")
        phraseLen = bufPtr - at;
        bufPtr += 1  // advance past closing term
        if (bufPtr < max) if (vm.mem[bufPtr++] != 0x20)
            throw Exception("Unexpected char after term")
        return Pair(at, phraseLen);
    }
}
