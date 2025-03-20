package kf.words.core

import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.words.custom.wCreateCustom.w_addr

object wCreate: IWordClass {
    override val name = "Create"
    override val description = "Data allocation and DOES"

    override val words
        get() = arrayOf(
            Word("CREATE", ::w_create),
            Word("DOES>", ::w_doesAngle, imm = true, compO = true),
            Word("DOES", ::w_does),
            Word("ALLOT", ::w_allot),
        )

    /**
     * CREATE
     * CORE
     * ( "<spaces>name" -- )
     * Skip leading space delimiters. Parse name delimited by a space. Create a definition for name with the execution semantics defined below. If the data-space pointer is not aligned, reserve enough data space to align it. The new data-space pointer defines name's data field. CREATE does not allocate data space in name's data field.
     *
     * name Execution:
     * ( -- a-addr )
     * a-addr is the address of name's data field. The execution semantics of name may be extended by using DOES>.
     */

    fun w_create(vm: ForthVM) {
        val name: String = vm.interp.getToken()
        val w = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = vm.dend,
            fn = ::w_addr)
        vm.dict.add(w)
    }


    /**  ( n -- ) Get n spaces in data section. */
    fun w_allot(vm: ForthVM) {
        val d = vm.dstk.pop()
        vm.dend += d
    }

    /**  does> : inside of compilation, adds "does" + "ret"
     *
     * Example:
     *
     *      : const create , does> @ ;
     *      42 const life
     *  then:
     *      life   => 42
     *
     *  `does>` modifies the definition of `const`` at the time of compiling it.
     *
     * */

    fun w_doesAngle(vm: ForthVM) {
        vm.appendWord("does")

        // don't append a ;s -- it's the same thing -- but the decompiler
        // stops printing a word def at that, and it's helpful to see the
        // "does part" for a word.
        vm.appendWord("exit")
    }

    /**  does: change most recent word from data to call-fn-with-data-num */

    fun w_does(vm: ForthVM) {
        val w: Word = vm.dict.last
        w.fn = vm.dict["addrcall"].fn
        w.cpos = vm.ip + 1
    }


}