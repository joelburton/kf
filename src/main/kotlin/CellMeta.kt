package kf


enum class CellMeta {
    Unknown,
    WordNum,
    JumpLoc,

    //    reg_base,
//    reg_verbosity,
//    reg_cend,
//    reg_dend,
    StringLit,
    NumLit;

    fun getExplanation(vm: ForthVM, v: Int): String {
        return when (this) {
            WordNum -> vm.dict[v].name
            JumpLoc -> "  --> ${v.addr}"
            Unknown, NumLit -> "$v ${v.hex} ${v.charRepr}"
            StringLit -> "$v (string length)"
//        CellMeta.reg_base -> generalFormat+" (reg: base)"
//        CellMeta.reg_verbosity -> generalFormat+" (reg: verbosity)"
//        CellMeta.reg_cend -> generalFormat+" (reg: cend)"
//        CellMeta.reg_dend -> generalFormat+" (reg: dend)"
        }
    }
}
