package kf.interfaces

interface IFScanner {
    /** Location in memory of start of scanner buffer (doesn't change). */
    val start: Int

    /** Location in memory of end of scanner buffer (doesn't change). */
    val end: Int

    /** Size of buffer (Doesn't change). */
    val size: Int

    /** Actual # of chars in buffer (both parsed & unparsed). */
    var nChars: Int

    /** Return string of most-recently-found token. */
    fun curToken(): String

    /** Reset buffer and fill it from string. */
    fun fill(str: String)

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
    fun parseName(): Pair<Int, Int>

    /** General parse for a term character.
     *
     * - skip nothing at start
     * - get all non-term chars
     *
     * with term='-'
     *    ...hello--.
     *    ^        ^  = tokPtr & vm.inPtr
     *
     * This is used to parse things like `." hi"` and such. Note that, per the
     * specs, *it does not* consume or require any whitespace after it.
     * So `." hi"10` could be successfully read as (string "hi", number 10)
     * if the call to this was followed by parseName.
     *
     **/
    fun parse(term: Char): Pair<Int, Int>

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
    fun wordParse(term: Char): Pair<Int, Int>

    /** Consume rest of line.
     *
     * Any parsing after this will always have len=0.
     *
     **/
    fun nextLine()
}