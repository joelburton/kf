package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass
import kf.addr
import kf.hex8


object WMemory : WordClass {
    override val name = "Memory"
    override val primitives get() = arrayOf(
        Word("allot", ::w_allot ) ,
        Word("cell", ::w_cell ) ,
        Word("cells", ::w_cells ) ,
        Word("unused", ::w_unused ) ,
        Word("on", ::w_on ) ,
        Word("off", ::w_off ) ,
        Word("erase", ::w_erase ) ,
        Word("fill", ::w_fill ) ,
        Word("cells+", ::w_cellsPlus),
        Word(".memconfig", ::w_dotMemConfig),
    )

    private fun w_cell(vm: ForthVM) {
        vm.dstk.push(1)
    }

    private fun w_cells(vm: ForthVM) {
        val size: Int = vm.dstk.pop()
        vm.dstk.push(size)
    }

//    private fun w_plusBang(vm: ForthVM) {
//        val addr: Int = vm.dstk.pop()
//        vm.mem[addr]++
//    }

    private fun w_unused(vm: ForthVM) {
        vm.dstk.push(vm.memConfig.dataEnd - vm.dend + 1)
    }

    // addr on  = set to true
    private fun w_on(vm: ForthVM) {
        vm.mem[vm.dstk.pop()] = ForthVM.Companion.TRUE
    }

    // addr off = set to false
    private fun w_off(vm: ForthVM) {
        vm.mem[vm.dstk.pop()] = ForthVM.Companion.FALSE
    }

    //addr u erase
    private fun w_erase(vm: ForthVM) {
        val len: Int = vm.dstk.pop()
        val startAt: Int = vm.dstk.pop()
        for (i in 0..len) {
            vm.mem[startAt + i] = 0
        }
    }

    // addr u c fill
    private fun w_fill(vm: ForthVM) {
        val fillWith: Int = vm.dstk.pop()
        val startAt: Int = vm.dstk.pop()
        val len: Int = vm.dstk.pop()
        for (i in 0..len) {
            vm.mem[startAt + i] = fillWith
        }
    }








    /**  ( n -- ) Get n spaces in data section. */
    fun w_allot(vm: ForthVM) {
        val d = vm.dstk.pop()
        vm.dend += d
    }






    fun w_cellsPlus(vm: ForthVM) {
        val addr = vm.dstk.pop()
        val num = vm.dstk.pop()
        vm.dstk.push(addr + num)
    }

    fun w_dotMemConfig(vm: ForthVM) {
        vm.memConfig.show()
    }
}