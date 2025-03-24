package kf

/** Scanner for parsing buffers.
 *
 * There are 3 different algos here:
 *
 * - parseName: used for `PARSE-NAME`
 * - parse: used for `PARSE`
 * - wordParse: used for `WORD` (A slightly weird algorithm)
 *
 * Generally, avoid `wordParse`; it is here because it is required by the
 * Forth word `WORD`, which itself should be avoided in favor of `PARSE-NAME`
 * or `PARSE`.
 *
 * In all cases, *it is not an error* if the scanning terminates without
 * finding a closing character. So, while it's a bit gross, Forth treats
 * stuff like:  `." no-close`  or  `( no-close`   as ok.
 *
 * The algorithms return a Pair(addr, len), but this can be turned into a
 * real string.

 * If the input buffer is already exhausted (at the end), no error is raised--
 * it just returns the pair with len=0.
 *
 * To explicitly consume everything and ignore it (like for "\" comments,
 * use `.nextLine()`.
 *
 */

class FScanner(val vm: ForthVM) {
    val start = vm.memConfig.interBufStart
    val end = vm.memConfig.interpBufEnd
    val size = end - start

    var nChars = 0  // # of chars in entire buffer
    var tokIdx = 0  // start idx of most-recently-found token
    var tokLen = 0  // length of same

    companion object {
        val whitespace = arrayOf(
            ' '.code,
            '\t'.code,
            '\n'.code,
            '\r'.code,
        )
    }

    val atEnd get() = vm.inPtr >= nChars

    /** Return string of most-recently-found token. */

    fun curToken() = Pair(start + tokIdx, tokLen).strFromAddrLen(vm)

    /** Reset */

    fun reset() {
        vm.inPtr = 0
        nChars = 0
        tokIdx = 0
        tokLen = 0
    }

    /** Reset buffer and fill it from string. */

    fun fill(str: String) {
        reset()
        for (char in str) {
            if (nChars > size) throw MemError("Buffer overflow")
            vm.mem[start + nChars++] = char.code
        }
    }

    /** Return string of entire buffer (useful for debugging.) */

    override fun toString() = Pair(start, nChars).strFromAddrLen(vm)

    /** Get whitespace-separated token.
     *
     * - skip over all whitespace at start
     * - get all non-space chars
     * - skip one space
     *
     *   ...hello...
     *      ^     ^   = tokPtr and vm.inPtr (tokLen=5, bufLen unchanged)
     *
     * This should be used to "get the next token". To "get the input
     * from this token", use `parse`.
     *
     **/

    fun parseName(): Pair<Int, Int> {
        while (vm.inPtr < nChars && vm.mem[start + vm.inPtr] in whitespace)
            vm.inPtr += 1
        tokIdx = vm.inPtr

        while (vm.inPtr < nChars && vm.mem[start + vm.inPtr] !in whitespace)
            vm.inPtr += 1
        tokLen = vm.inPtr - tokIdx

        vm.inPtr += 1

        return Pair(start + tokIdx, tokLen)
    }

    /** General parse for a term character.
     *
     * - skip nothing at start
     * - get all non-term chars
     *
     * with term="
     *    ...hello"".
     *    ^        ^  = tokPtr & vm.inPtr
     *
     * This is used to parse things like `." hi"` and such. Note that, per the
     * specs, *it does not* consume or require any whitespace after it.
     * So `." hi"10` could be successfully read as (string "hi", number 10)
     * if the call to this was followed by parseName.
     *
     **/

    fun parse(term: Char): Pair<Int, Int> {
        tokIdx = vm.inPtr
        while (vm.inPtr < nChars && vm.mem[start + vm.inPtr] != term.code)
            vm.inPtr += 1

        tokLen = vm.inPtr - tokIdx

        // skip the terminator, but don't require or skip space after it.
        vm.inPtr += 1

        return Pair(start + tokIdx, tokLen)
    }

    /** General parse for an ending character, using `WORD` algo.
     *
     *  with term=x
     *    xxHELLOxx.
     *      ^     ^  = tokPtr & vm.inPtr
     *
     *  with term=' ', same but term=any-whitespace-character.
     *
     *  This is a weird algorithm, and be avoided except for the Forth word
     *  `WORD`.
     *
     **/

    fun wordParse(term: Char): Pair<Int, Int> {
        var terms = if (term == ' ') whitespace else arrayOf(term.code)

        while (vm.inPtr < nChars && vm.mem[start + vm.inPtr] in terms)
            vm.inPtr += 1

        tokIdx = vm.inPtr
        while (vm.inPtr < nChars && vm.mem[start + vm.inPtr] !in terms)
            vm.inPtr += 1

        tokLen = vm.inPtr - tokIdx
        vm.inPtr += 1

        return Pair(start + tokIdx, tokLen)
    }

    /** Consume rest of line.
     *
     * Any parsing after this will always have len=0.
     *
     **/

    fun nextLine() {
        nChars = 0
    }
}
