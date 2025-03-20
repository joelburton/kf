package kf.words.core

import kf.CellMeta
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.strFromAddrLen

object wVariables: IWordClass {
    override val name = "Variables"
    override val description = "Variables & constants"

    override val words
        get() = arrayOf(
            Word("CONSTANT", ::w_constant),
            Word("VARIABLE", ::w_variable),
        )

    /** CONSTANT     CORE
     *
     * ( x "<spaces>name" -- )
     *
     * Skip leading space delimiters. Parse name delimited by a space. Create
     * a definition for name with the execution semantics defined below.
     *
     * name is referred to as a "constant".
     *
     * name Execution:
     * ( -- x )
     * Place x on the stack.
     */

    fun w_constant(vm: ForthVM) {
        val data = vm.dstk.pop()
        val name =  vm.interp.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = vm.dend,
            fn = ::w_parenConstant)
        vm.dict.add(w)
        vm.cellMeta[vm.dend] = CellMeta.NumLit
        vm.mem[vm.dend++] = data

    }

    /** (CONSTANT) */

    fun w_parenConstant(vm: ForthVM) {
        val data = vm.mem[vm.currentWord.dpos]
        vm.dstk.push(data)
    }

    /**
     * VARIABLE
     * CORE
     * ( "<spaces>name" -- )
     * Skip leading space delimiters. Parse name delimited by a space. Create a definition for name with the execution semantics defined below. Reserve one cell of data space at an aligned address.
     *
     * name is referred to as a "variable".
     *
     * name Execution:
     * ( -- a-addr )
     * a-addr is the address of the reserved cell. A program is responsible for initializing the contents of the reserved cell.
     */

    fun w_variable(vm: ForthVM) {
        wCreate.w_create(vm)
        vm.dend += 1
    }

}