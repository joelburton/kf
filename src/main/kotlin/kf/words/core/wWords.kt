package kf.words.core

import kf.D
import kf.ForthVM
import kf.dict.IWordModule
import kf.dict.Word
import kf.mem.appendLit
import kf.strFromAddrLen
import kf.strFromCSAddr

object wWords : IWordModule {
    override val name = "kf.words.core.wWords"
    override val description = "Acting on words"

    override val words
        get() = arrayOf(
            Word("[']", ::w_bracketTick, imm = true, compO = true),
            Word("'", ::w_tick),
            Word("FIND", ::w_find),
            Word(">BODY", ::w_toBody),
        )

    /**
     * `[']` ( -- xt ) Get xt of word
     *
     * Compilation:
     *      ( "<spaces>name" -- )
     *
     * Run-time: ( -- xt )
     * Place name's execution token xt on the stack. The execution token
     * returned by the compiled phrase "['] X" is the same value returned by
     * "' X" outside of compilation state.
     */

    fun w_bracketTick(vm: ForthVM) {
        val token = vm.source.scanner.parseName().strFromAddrLen(vm)
        if (D) vm.dbg(3, "w_bracketTick: token='$token'")
        val wn: Int = vm.dict[token].wn
        vm.appendLit(wn)
    }

    /** `'` ( "<spaces>name" -- xt ) Get xt of word */

    fun w_tick(vm: ForthVM) {
        val token = vm.source.scanner.parseName().strFromAddrLen(vm)
        val wn = vm.dict[token].wn
        vm.dstk.push(wn)
    }

    /**
     * `FIND` ( c-addr -- c-addr 0 | xt 1 | xt -1 ) Find definition
     *
     * Find the definition named in the counted string at c-addr. If the
     * definition is not found, return c-addr and zero. If the definition is
     * found, return its execution token xt. If the definition is immediate,
     * also return one (1), otherwise also return minus-one (-1). For a given
     * string, the values returned by FIND while compiling may differ from those
     * returned while not compiling.
     */

    fun w_find(vm: ForthVM) {
        val addr = vm.dstk.pop()
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

    /** `>BODY` ( xt -- a-addr ) Get data-field address corresponding to xt. */

    fun w_toBody(vm: ForthVM) {
        val w = vm.dict[vm.dstk.pop()]
        vm.dstk.push(w.dpos)
    }
}