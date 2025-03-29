package kf.words.custom

import kf.ForthVM
import kf.dict.IWordModule
import kf.dict.Word


object wMemoryCustom: IWordModule {
    override val name = "kf.words.custom.wMemoryCustom"
    override val description = "Memory management"
     override val words = arrayOf(
        Word(",,", ::w_commaComma),
         Word("CELL", ::w_cell ) ,
         Word("ON", ::w_on ) ,
         Word("OFF", ::w_off ) ,
         Word("CELLS+", ::w_cellsPlus),

         )

    fun w_commaComma(vm: ForthVM) {
        vm.mem[vm.cend++] = vm.dstk.pop()
    }

    private fun w_cell(vm: ForthVM) {
        vm.dstk.push(1)
    }

    // addr on  = set to true
    private fun w_on(vm: ForthVM) {
        vm.mem[vm.dstk.pop()] = ForthVM.Companion.TRUE
    }

    // addr off = set to false
    private fun w_off(vm: ForthVM) {
        vm.mem[vm.dstk.pop()] = ForthVM.Companion.FALSE
    }

    fun w_cellsPlus(vm: ForthVM) {
        val addr = vm.dstk.pop()
        val num = vm.dstk.pop()
        vm.dstk.push(addr + num)
    }


}