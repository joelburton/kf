package kf.words.core

import kf.mem.CellMeta
import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord
import kf.strFromAddrLen

object wVariables: IWordModule {
    override val name = "kf.words.core.wVariables"
    override val description = "Variables & constants"

    override val words
        get() = arrayOf<IWord>(
            Word("CONSTANT", ::w_constant),
            Word("VARIABLE", ::w_variable),
        )

    /** `CONSTANT` ( x "<spaces>name" -- ) Create and define constant
     *
     * Creation:  ( x "<spaces>name" -- )
     * Execution: ( -- x ) Place x on stack
     */

    fun w_constant(vm: IForthVM) {
        val data = vm.dstk.pop()
        val name =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            name,
            cpos = 0xffff,
            dpos = vm.dend,
            fn = ::parenConstant)
        vm.dict.add(w)
        vm.cellMeta[vm.dend] = CellMeta.NumLit
        vm.mem[vm.dend++] = data
    }

    /** Return the data for the constant at currentWord */

    private fun parenConstant(vm: IForthVM) {
        val data = vm.mem[vm.currentWord.dpos]
        vm.dstk.push(data)
    }

    /**
     * `VARIABLE` ( "<spaces>name" -- ) Create variable
     *
     * Execution: ( -- a-addr ) a-addr is the address of the reserved cell.
     */

    fun w_variable(vm: IForthVM) {
        wCreate.w_create(vm)
        vm.dend += 1
    }
}