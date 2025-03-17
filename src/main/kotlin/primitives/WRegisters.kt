package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass

object WRegisters : WordClass {
    override val name = "Registers"
    override val primitives get() = arrayOf(
        Word("r:cstart", ::w_cstart),
        Word("r:cend", ::w_cend),
        Word("r:dstart", ::w_dstart),
        Word("r:dend", ::w_dend),
        Word("r:base", ::w_base),
        Word("r:verbosity", ::w_verbosity),
        Word("r:interp-state", ::w_interpState),
    )

    fun w_cstart(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_CSTART)
    }

    fun w_cend(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_CEND)
    }

    fun w_dstart(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_DSTART)
    }

    fun w_dend(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_DEND)
    }

    fun w_base(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_BASE)
    }

    fun w_verbosity(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_VERBOSITY)
    }

    fun w_interpState(vm: ForthVM) {
        vm.dstk.push(ForthVM.REG_INTERP_STATE)
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