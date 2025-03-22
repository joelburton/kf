package kf.words.core.ext

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.strFromAddrLen

object wWordsExt : IWordClass {
    override val name = "core.ext.wordsExt"
    override val description = "Acting on words"

    override val words
        get() = arrayOf(
            Word("MARKER", ::w_marker),
        )


    /**  `marker` IO ( in:"name" -- : create word which will forget itself )
     *
     * For example:
     *
     * : aa ;
     * marker foo
     * : bb ;
     * foo
     *
     * Will forget back to (and including) when the "foo" marker was made.
     */
    fun w_marker(vm: ForthVM) {
        val wCall = vm.dict["call"]
        val newName: String =  vm.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            newName,
            fn = wCall.fn,
            cpos = vm.cend,
            dpos = Word.Companion.NO_ADDR
        )
        vm.dict.add(w)
        vm.appendLit(w.wn)
        vm.appendWord("wn-forget")
        vm.appendWord(";s")
    }

}