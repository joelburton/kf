package kf.words

import kf.ForthVM
import kf.IWordClass
import kf.Word

object wRegisters : IWordClass {
    override val name = "Registers"
    override val description = "System registers"
    override val words get() = arrayOf(
        Word("R:CSTART", ::w_cstart),
        Word("R:CEND", ::w_cend),
        Word("R:DSTART", ::w_dstart),
        Word("R:DEND", ::w_dend),
        Word("R:BASE", ::w_base),
        Word("R:VERBOSITY", ::w_verbosity),
        Word("R:INTERP-STATE", ::w_interpState),
    )

    fun w_cstart(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_CSTART)
    }

    fun w_cend(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_CEND)
    }

    fun w_dstart(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_DSTART)
    }

    fun w_dend(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_DEND)
    }

    fun w_base(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_BASE)
    }

    fun w_verbosity(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_VERBOSITY)
    }

    fun w_interpState(vm: ForthVM) {
        vm.dstk.push(ForthVM.Companion.REG_STATE)
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