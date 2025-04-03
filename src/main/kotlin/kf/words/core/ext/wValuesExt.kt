package kf.words.core.ext

import kf.*
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord
import kf.mem.CellMeta

object wValuesExt: IWordModule {
    override val name = "kf.words.core.ext.wValuesExt"
    override val description = "Flexible variables"

    override val words: Array<IWord>
        get() = arrayOf(
            Word("VALUE", ::w_value),
            Word("TO", ::w_to)
        )

    // "values" are the same, implementation-wise, as constants.
    // The only difference is that they have different identity for
    // their inner functions (parenValue vs parenConstant), and
    // "to" relies on that to make sure people don't casually re-value
    // a constant.

    /** `VALUE`  ( x "<spaces>name" -- ) Create and define values
     *
     * Creation:  ( x "<spaces>name" -- )
     * Execution: ( -- x ) Place x on stack
     */

    fun w_value(vm: IForthVM) {
        val data = vm.dstk.pop()
        val name =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = Word(
            name,
            cpos = 0xffff,
            dpos = vm.dend,
            fn = ::parenValue
        )
        vm.dict.add(w)
        vm.cellMeta[vm.dend] = CellMeta.NumLit
        vm.mem[vm.dend++] = data
    }

    /** Return the data for the constant at currentWord */

    private fun parenValue(vm: IForthVM) {
        val data = vm.mem[vm.currentWord.dpos]
        vm.dstk.push(data)
    }

    /** `TO` ( in:"name" x -- ) Assign x to value */

    fun w_to(vm: IForthVM) {
        val name =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val word = vm.dict[name]
        val x = vm.dstk.pop()

        if (word.fn != ::parenValue) {
            throw WordValueAssignError("Not a value: $name")
        }
        vm.mem[word.dpos] = x
    }

}