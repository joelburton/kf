package kf.words.core.ext

import com.github.ajalt.mordant.terminal.warning
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.strFromAddrLen
import kf.w_notImpl

object wDeferExt : IWordClass {
    override val name = "core.ext.deferExt"
    override val description = "Deferred words"

    override val words
        get() = arrayOf(
            Word("DEFER", ::w_defer),
//        Word("deferred-word", ::deferred),
            Word("DEFER!", ::w_deferStore),
            Word("DEFER@", ::w_deferFetch),
            Word("IS", ::w_is),
            Word("ACTION-OF", ::w_notImpl)
        )


    fun deferred(vm: ForthVM) {
        vm.io.warning("Use of uninitialized deferred word: ${vm.currentWord}")
    }

    /** `defer` ( "word" -- : create word pointing to uninitialized fn ) */

    fun w_defer(vm: ForthVM) {
        val name = vm.interp.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = Word.NO_ADDR,
            fn = ::deferred,
            deferToWn = vm.dict["deferred-word"].wn
        )
        vm.dict.add(w)
    }

    /** `is` ( wn "word" -- : associates wn-word to "word" word ) */

    fun changeDeferPointer(vm: ForthVM, deferredWord: Word, sourceWord: Word) {
        deferredWord.cpos = sourceWord.cpos
        deferredWord.dpos = sourceWord.dpos
        deferredWord.fn = sourceWord.fn
        deferredWord.deferToWn = sourceWord.wn
    }

    fun w_is(vm: ForthVM) {
        val sourceWord = vm.dict[vm.dstk.pop()]
        val token = vm.interp.scanner.parseName().strFromAddrLen(vm)
        val deferredWord =
            vm.dict[token]
        changeDeferPointer(vm, deferredWord, sourceWord)
    }

    fun w_deferStore(vm: ForthVM) {
        val deferredWord = vm.dict[vm.dstk.pop()]
        val sourceWord = vm.dict[vm.dstk.pop()]
        changeDeferPointer(vm, deferredWord, sourceWord)
    }

    fun w_deferFetch(vm: ForthVM) {
        val deferredWord = vm.dict[vm.dstk.pop()]
        vm.dstk.push(deferredWord.deferToWn ?: deferredWord.wn)
    }
}