package kf.words.custom

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM


object wMemoryCustom : IWordModule {
    override val name = "kf.words.custom.wMemoryCustom"
    override val description = "Memory management"
    override val words = arrayOf<Word>(
        Word(",,", ::w_commaComma),
        Word("CELL", ::w_cell),
        Word("ON", ::w_on),
        Word("OFF", ::w_off),
        Word("CELLS+", ::w_cellsPlus),

        )

    fun w_commaComma(vm: IForthVM) {
        vm.mem[vm.cend++] = vm.dstk.pop()
    }

    fun w_cell(vm: IForthVM) {
        vm.dstk.push(1)
    }

    // addr on  = set to true
    fun w_on(vm: IForthVM) {
        vm.mem[vm.dstk.pop()] = ForthVM.Companion.TRUE
    }

    // addr off = set to false
    fun w_off(vm: IForthVM) {
        vm.mem[vm.dstk.pop()] = ForthVM.Companion.FALSE
    }

    fun w_cellsPlus(vm: IForthVM) {
        val addr = vm.dstk.pop()
        val num = vm.dstk.pop()
        vm.dstk.push(addr + num)
    }


}