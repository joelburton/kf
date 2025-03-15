package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass

object WRegisters : WordClass {
    override val name = "Registers"
    override val primitives: Array<Word> = arrayOf(
        Word("cstart", ::w_cstart),
        Word("cend", ::w_cend),
        Word("dstart", ::w_dstart),
        Word("dend", ::w_dend),
        Word("base", ::w_base),
        Word("verbosity", ::w_verbosity),
        Word("interp-state", ::w_interpState),
    )

    fun w_cstart(vm: ForthVM) {
        vm.dstk.push(vm.mem[ForthVM.Companion.REG_CSTART])
    }

    fun w_cend(vm: ForthVM) {
        vm.dstk.push(vm.mem[ForthVM.Companion.REG_CEND])
    }

    fun w_dstart(vm: ForthVM) {
        vm.dstk.push(vm.mem[ForthVM.Companion.REG_DSTART])
    }

    fun w_dend(vm: ForthVM) {
        vm.dstk.push(vm.mem[ForthVM.Companion.REG_DEND])
    }

    fun w_base(vm: ForthVM) {
        vm.dstk.push(vm.mem[ForthVM.Companion.REG_BASE])
    }

    fun w_verbosity(vm: ForthVM) {
        vm.dstk.push(vm.mem[ForthVM.Companion.REG_VERBOSITY])
    }

    fun w_interpState(vm: ForthVM) {
        vm.dstk.push(vm.mem[ForthVM.Companion.REG_INTERP_STATE])
    }


//    private fun makeRegWord(name: String, addr: Int) {
//        return Word("r:$name") { _ -> register(name, addr) }
//
//    }
//    private fun register(name: String, addr: Int) {
//        if (D) vm.dbg(3, "w_reg-${name}")
//        vm.dstk.push(addr)
//    }
}