package kf

class WComments(val vm: ForthVM) {
    val primitives: Array<Word> = arrayOf<Word>(
        Word("\\", immediate = true ) { _ -> w_backslash() },
        Word("(", immediate = true) { _ -> w_paren_comment() },
    )

    /**  ( -- : handles parentheses comments )
     *
     * These can only be on one line; if a comment isn't closed by EOL,
     * the scanner will throw an error.
     */
    fun w_paren_comment() {
        if (D) vm.dbg("w_paren_comment")
        if (vm.interpScanner!!.findInLine(" \\)") == null) {
            throw ForthError("Parentheses comment not closed")
        }
    }

    /**  ( -- : handles backslash comments )
     */
    fun w_backslash() {
        if (D) vm.dbg("w_backslash")
        vm.interpScanner!!.nextLine()
    }
}