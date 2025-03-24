package kf.words.core.ext

import kf.*

/** Deferrable words.
 *
 * Our implementation exceeds the capabilities and flexibility of the spec:
 * any word can have its definition changed; it doesn't need to be created as
 * a deferred word.
 */

object wDeferExt : IWordModule {
    override val name = "kf.words.core.ext.wDeferExt"
    override val description = "Deferred words"

    override val words
        get() = arrayOf(
            Word("DEFER", ::w_defer),
            Word("(DEFERRED-WORD)", ::deferred),
            Word("DEFER!", ::w_deferStore),
            Word("DEFER@", ::w_deferFetch),
            Word("IS", ::w_is),
            Word("ACTION-OF", ::w_actionOf)
        )


    private fun deferred(vm: ForthVM) {
        throw ForthDeferredWordError(
            "Use of uninitialized deferred word: ${vm.currentWord}")
    }

    /** `defer` ( "word" -- : create word pointing to uninitialized fn ) */

    fun w_defer(vm: ForthVM) {
        val name = vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = Word.NO_ADDR,
            fn = ::deferred,
            deferToWn = vm.dict["(DEFERRED-WORD)"].wn
        )
        vm.dict.add(w)
    }

    // Internal use by other functions here

    private fun changeDeferPointer(vm: ForthVM, deferW: Word, srcW: Word) {
        deferW.cpos = srcW.cpos
        deferW.dpos = srcW.dpos
        deferW.fn = srcW.fn
        deferW.deferToWn = srcW.wn
    }

    /** `IS` ( wn "word" -- : sets "word" to use wn as its xt ) */

    fun w_is(vm: ForthVM) {
        val srcW = vm.dict[vm.dstk.pop()]
        val token = vm.source.scanner.parseName().strFromAddrLen(vm)
        val deferW = vm.dict[token]
        changeDeferPointer(vm, deferW, srcW)
    }

    /** `DEFER! ( xt1 xt2 -- ) Set deferred word xt2 to xt1 */

    fun w_deferStore(vm: ForthVM) {
        val deferW = vm.dict[vm.dstk.pop()]
        val sourceWord = vm.dict[vm.dstk.pop()]
        changeDeferPointer(vm, deferW, sourceWord)
    }

    /** `DEFER@` ( xt -- xt ) Push wn of deferral */

    fun w_deferFetch(vm: ForthVM) {
        val deferW = vm.dict[vm.dstk.pop()]
        vm.dstk.push(deferW.deferToWn ?: deferW.wn)
    }

    /** `ACTION-OF` ( "name" -- xt ) Push wn of deferral */

    fun w_actionOf(vm: ForthVM) {
        val s = vm.source.scanner.parseName().strFromAddrLen(vm)
        val deferW = vm.dict[s]
        vm.dstk.push(deferW.deferToWn ?: deferW.wn)
    }
}