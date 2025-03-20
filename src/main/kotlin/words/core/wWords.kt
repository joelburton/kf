package kf.words.core

import kf.D
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.strFromCSAddr

object wWords : IWordClass {
    override val name = "Words"
    override val description = "Acting on words"

    override val words
        get() = arrayOf(
            Word("'", ::w_tick),
            Word("FIND", ::w_find),
            Word(">BODY", ::w_toBody),
            Word("[']", ::w_bracketTick, imm = true, compO = true),
        )

    /**
     * [']   CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     *      ( "<spaces>name" -- )
     * Skip leading space delimiters. Parse name delimited by a space. Find
     * name. Append the run-time semantics given below to the current definition.
     *
     * An ambiguous condition exists if name is not found.
     *
     * Run-time:
     *      ( -- xt )
     * Place name's execution token xt on the stack. The execution token
     * returned by the compiled phrase "['] X" is the same value returned by
     * "' X" outside of compilation state.
     */

    fun w_bracketTick(vm: ForthVM) {
        val token: String = vm.interp.getToken()
        if (D) vm.dbg(3, "w_bracketTick: token='$token'")
        val wn: Int = vm.dict.getNum(token)
        vm.appendLit(wn)
    }

    /** ' CORE
     *
     *      ( "<spaces>name" -- xt )
     *
     * Skip leading space delimiters. Parse name delimited by a space. Find
     * name and return xt, the execution token for name. An ambiguous condition
     * exists if name is not found. When interpreting, ' xyz EXECUTE is
     * equivalent to xyz.
     */

    fun w_tick(vm: ForthVM) {
        val token: String = vm.interp.getToken()
        val wn = vm.dict.getNum(token)
        vm.dstk.push(wn)
    }

    /**
     * FIND
     *
     * ( c-addr -- c-addr 0 | xt 1 | xt -1 )
     *
     * Find the definition named in the counted string at c-addr. If the
     * definition is not found, return c-addr and zero. If the definition is
     * found, return its execution token xt. If the definition is immediate,
     * also return one (1), otherwise also return minus-one (-1). For a given
     * string, the values returned by FIND while compiling may differ from those
     * returned while not compiling.
     */

    private fun w_find(vm: ForthVM) {
        val addr: Int = vm.dstk.pop()
        val token = addr.strFromCSAddr(vm)
        if (D) vm.dbg(3, "w_find: token='$token'")
        val w = vm.dict.getSafe(token)
        if (w == null) {
            if (D) vm.dbg(3, "w_find: not found")
            vm.dstk.push(addr, 0)
        } else if (w.imm) {
            if (D) vm.dbg(3, "w_find: immediate")
            vm.dstk.push(w.wn, 1)
        } else {
            if (D) vm.dbg(3, "w_find: not immediate")
            vm.dstk.push(w.wn, -1)
        }
    }

    /**
     * >BODY    CORE
     *
     * ( xt -- a-addr )
     *
     * a-addr is the data-field address corresponding to xt. An ambiguous
     * condition exists if xt is not for a word defined via CREATE.
     */

    fun w_toBody(vm: ForthVM) {
        val w = vm.dict[vm.dstk.pop()]
        vm.dstk.push(w.dpos)
    }
}