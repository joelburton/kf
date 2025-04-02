package kf.words.core

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IWord
import kf.strFromAddrLen
import kf.words.core.wFunctions.w_call

object wCreate: IWordModule {
    override val name = "kf.words.core.wCreate"
    override val description = "Data allocation and DOES"

    override val words
        get() = arrayOf<IWord>(
            Word("CREATE", ::w_create),
            Word("DOES>", ::w_doesAngle, imm = true, compO = true),
            Word("(DOES)", ::w_parenDoes),
            Word("(ADDR)", ::w_parenAddr, hidden = true),
            Word("(ADDRCALL)", ::w_parenAddrCall, compO = true, hidden = true),
            )

    /** `CREATE`
     *
     * Compilation: ( C: "<spaces>name" -- )
     *  Create name. Do not allocate any data, but assign dpos to DATA.
     *
     * Execution of name: ( -- a-addr ) Get address of DATA for word.
     */

    fun w_create(vm: ForthVM) {
        val name =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = vm.dend,
            fn = vm.dict["(ADDR)"].fn)
        vm.dict.add(w)
    }

    /** `DOES>` IM CO ( -- ) Append DOES/EXIT to current definition.
     *
     * Example:
     *
     *      : const create , does> @ ;
     *      42 const life
     *  then:
     *      life   => 42
     *
     *  `DOES` modifies the definition of `const`` at the time of compiling it.
     *
     * */

    fun w_doesAngle(vm: ForthVM) {
        vm.appendWord("(DOES)")

        // don't append a ;s -- it's the same thing -- but the decompiler
        // stops printing a word def at that, and it's helpful to see the
        // "does part" for a word.
        vm.appendWord("EXIT")
    }

    /** `(DOES)` ( -- ) Change most-recent-word to call-fn-with-data-num */

    fun w_parenDoes(vm: ForthVM) {
        val w: Word = vm.dict.last
        w.fn = vm.dict["(ADDRCALL)"].fn
        w.cpos = vm.ip + 1
    }


    /** `(ADDR)` For pure-data words, like "create age 1 allot" (ie variable) */

    fun w_parenAddr(vm: ForthVM) {
        val addr: Int = vm.currentWord.dpos
        vm.dstk.push(addr)
    }

    /**`(ADDRCALL)` ( -- addr : w/currWord: push dpos, then call it )'
     *
     * Used for all "does" words (it's the "function" for a constant). This
     * word will need to have both a dpos and a cpos, and only words
     * made by create + does will have that.
     * */

    fun w_parenAddrCall(vm: ForthVM) {
        w_parenAddr(vm)
        w_call(vm)
    }

}