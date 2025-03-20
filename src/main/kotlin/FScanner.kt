package kf

import kf.ForthError

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

class FScanner(val vm: ForthVM, val bufStart: Int, val bufEnd: Int) {
    var bufPtr = bufStart  // buffer addr
    var bufLen = 0  // length of entire buffer
    var tokPtr = bufStart  // start addr of most-recently-found token
    var tokLen = 0  // length of same

    companion object {
        val whitespace = arrayOf(
            ' '.code,
            '\t'.code,
            '\n'.code,
            '\r'.code,
        )
    }

    val atEnd get() = bufPtr >= bufStart + bufLen

    /** Return string of most-recently-found token. */

    fun curToken() = Pair(tokPtr, tokLen).strFromAddrLen(vm)

    /** Reset */

    fun reset() {
        bufPtr = bufStart
        bufLen = 0
        tokPtr = bufStart
        tokLen = 0
    }

    /** Reset buffer and fill it from string. */

    fun fill(str: String) {
        reset()
        for (char in str) {
            if (bufLen > bufEnd - bufStart) throw MemError("Buffer overflow")
            vm.mem[bufStart + bufLen++] = char.code
        }
    }

    /** Return string of entire buffer (useful for debugging.) */

    override fun toString() = Pair(bufStart, bufLen).strFromAddrLen(vm)

    /** Get whitespace-separated token.
     *
     * - skip over all whitespace at start
     * - get all non-space chars
     * - skip one space
     *
     *   ...hello...
     *      ^     ^   = tokPtr and bufPtr (tokLen=5, bufLen unchanged)
     *
     * This should be used to "get the next token". To "get the input
     * from this token", use `parse`.
     *
     **/

    fun parseName(): Pair<Int, Int> {
        val max = bufStart + bufLen

        while (bufPtr < max && vm.mem[bufPtr] in whitespace) bufPtr += 1
        tokPtr = bufPtr

        while (bufPtr < max && vm.mem[bufPtr] !in whitespace) bufPtr += 1
        tokLen = bufPtr - tokPtr

        if (bufPtr < max) bufPtr += 1

        return Pair(tokPtr, tokLen)
    }

    /** General parse for a term character.
     *
     * - skip nothing at start
     * - get all non-term chars
     *
     * with term="
     *    ...hello"".
     *    ^        ^  = tokPtr & bufPtr
     *
     * This is used to parse things like `." hi"` and such. Note that, per the
     * specs, *it does not* consume or require any whitespace after it.
     * So `." hi"10` could be successfully read as (string "hi", number 10)
     * if the call to this was followed by parseName.
     *
     **/

    fun parse(term: Char): Pair<Int, Int> {
        val max = bufStart + bufLen

        tokPtr = bufPtr
        while (bufPtr < max && vm.mem[bufPtr] != term.code) bufPtr += 1

        tokLen = bufPtr - tokPtr

        // skip the terminator, but don't require or skip space after it.
        if (bufPtr < max) bufPtr += 1

        return Pair(tokPtr, tokLen)
    }

    /** General parse for an ending character, using `WORD` algo.
     *
     *  with term=x
     *    xxHELLOxx.
     *      ^     ^  = tokPtr & bufPtr
     *
     *  with term=' ', same but term=any-whitespace-character.
     *
     *  This is a weird algorithm, and be avoided except for the Forth word
     *  `WORD`.
     *
     **/

    fun wordParse(term: Char): Pair<Int, Int> {
        var terms = if (term == ' ') whitespace else arrayOf(term.code)
        val max = bufStart + bufLen

        while (bufPtr < max && vm.mem[bufPtr] in terms) bufPtr += 1

        tokPtr = bufPtr
        while (bufPtr < max && vm.mem[bufPtr] !in terms) bufPtr += 1

        tokLen = bufPtr - tokPtr
        if (bufPtr < max) bufPtr += 1

        return Pair(tokPtr, tokLen)
    }

    /** Consume rest of line.
     *
     * Any parsing after this will always have len=0.
     *
     **/

    fun nextLine() {
        bufLen = 0
    }
}
