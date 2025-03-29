package kf.words.core.ext

import kf.ForthVM
import kf.dict.IWordModule
import kf.dict.Word
import kf.strFromAddrLen
import kf.words.core.wFunctions

object wWordsExt : IWordModule {
    override val name = "kf.words.core.ext.wWordsExt"
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
        val newName: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            newName,
            fn = wFunctions::w_call,
            cpos = vm.cend,
            dpos = Word.Companion.NO_ADDR
        )
        vm.dict.add(w)
        vm.appendLit(w.wn)
        vm.appendWord(".WN-FORGET")
        vm.appendWord(";S")
    }

}