package kf.words.core.ext

import kf.dict.NO_ADDR
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.mem.appendLit
import kf.mem.appendWord
import kf.strFromAddrLen
import kf.words.core.wFunctions

object wWordsExt : IWordModule {
    override val name = "kf.words.core.ext.wWordsExt"
    override val description = "Acting on words"

    override val words: Array<Word>
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
    fun w_marker(vm: IForthVM) {
        val newName: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            newName,
            fn = wFunctions::w_call,
            cpos = vm.cend,
            dpos = NO_ADDR,
        )
        vm.dict.add(w)
        vm.appendLit(w.wn)
        vm.appendWord(".WN-FORGET")
        vm.appendWord(";S")
    }

}