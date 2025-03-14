package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass

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

object WComments : WordClass {
    override val name = "Comments"

    override val primitives = arrayOf(
        Word("\\", imm = true, fn = ::w_backslashComment),
        Word("(", imm = true, fn = ::w_parenComment),
    )

    /**  ( -- : handles parentheses comments )
     *
     * These can only be on one line; if a comment isn't closed by EOL,
     * the scanner will throw an error.
     */
    fun w_parenComment(vm: ForthVM) {
        vm.interpScanner.parse(')')
    }


    /**  ( -- : handles backslash comments )
     */
    fun w_backslashComment(vm: ForthVM) {
        vm.interpScanner.nextLine()
    }
}