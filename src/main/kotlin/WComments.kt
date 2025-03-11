package kf

class WComments(val vm: ForthVM) : WordClass {
    override val name = "Comments"

    override val primitives: Array<Word> = arrayOf<Word>(
        Word("\\", imm = true ) { _ -> w_backslashComment() },
        Word("(", imm = true) { _ -> w_parenComment() },
    )

    /**  ( -- : handles parentheses comments )
     *
     * These can only be on one line; if a comment isn't closed by EOL,
     * the scanner will throw an error.
     */
    fun w_parenComment() {
        if (D) vm.dbg("w_parenComment")
        if (vm.interpScanner!!.findInLine(" \\)") == null) {
            throw ParseError("Parenthesis comment not closed")
        }
    }

    /**  ( -- : handles backslash comments )
     */
    fun w_backslashComment() {
        if (D) vm.dbg("w_backslashComment")
        vm.interpScanner!!.nextLine()
    }
}