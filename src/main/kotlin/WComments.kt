package kf

//interface TestWord {
//    val name: String
//    val doc: String
//    var immediate: Boolean
//
//    fun print(vm: ForthVM) {
//        print("hey")
//    }
//    fun call(vm: ForthVM) {
//        print("calling $name")
//        run(vm)
//        print("done")
//    }
//     fun run(vm: ForthVM)
//}
//
//object ParenComment : TestWord {
//    override val name = "parenComment"
//    override val doc = "my doc"
//    override var immediate = false
//    override fun run(vm: ForthVM) {
//        print(vm)
//        immediate = true
//    }
//}
//
//object SParenComment : TestWord {
//    override val name = "parenComment"
//    override val doc = "my doc"
//    override var immediate = false
//    override fun run(vm: ForthVM) = ParenComment.run(vm)
//}
//
//fun main() {
//    val primitives = Comments().primitives
//    primitives.forEach {
//        it.call(ForthVM())
//    }
//}
//
//class Comments {
//    val primitives: Array<TestWord> = arrayOf<TestWord>(
//        ParenComment,
//    )
//}

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
        if (vm.interpScanner!!.findInLine(" \\)") == null) {
            throw ParseError("Parenthesis comment not closed")
        }
    }

    /**  ( -- : handles backslash comments )
     */
    fun w_backslashComment() {
        vm.interpScanner!!.nextLine()
    }
}