package kf


class WRegisters(val vm: ForthVM) : WordClass {
    override val name = "Registers"
    override val primitives: Array<Word> = arrayOf(
        makeRegWord("cstart", ForthVM.REG_CSTART),
        makeRegWord("cend", ForthVM.REG_CEND),
        makeRegWord("cstart", ForthVM.REG_DSTART),
        makeRegWord("dend", ForthVM.REG_DEND),
        makeRegWord("base", ForthVM.REG_BASE),
        makeRegWord("verbosity", ForthVM.REG_VERBOSITY),
        makeRegWord("interp-state", ForthVM.REG_INTERP_STATE),
    )

    private fun makeRegWord(name: String, addr: Int) =
        Word("r:$name") { _ -> register(name, addr) }

    private fun register(name: String, addr: Int) {
        if (D) vm.dbg(3, "w_reg-${name}")
        vm.dstk.push(addr)
    }
}