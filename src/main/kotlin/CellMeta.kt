package kf


/** What sort of information does this cell in memory hold?
 *
 * This isn't used by the VM itself at all, and isn't required to be filled in.
 * However, it helps the smart disassembler (`SEE`) to show more useful info.
 *
 */
enum class CellMeta {
    Unknown,
    WordNum,
    JumpLoc,
    CharLit,

    //    reg_base,
//    reg_verbosity,
//    reg_cend,
//    reg_dend,
    StringLen,
    NumLit;

    fun getExplanation(vm: ForthVM, v: Int, k: Int): String {
        return when (this) {
            WordNum -> vm.dict[v].name
            JumpLoc -> "  --> $v to ${(k + v).addr}"
            Unknown, NumLit -> "$v ${v.hex} ${v.charRepr}"
            StringLen -> "$v (string length)"
            CharLit -> v.charRepr
//        CellMeta.reg_base -> generalFormat+" (reg: base)"
//        CellMeta.reg_verbosity -> generalFormat+" (reg: verbosity)"
//        CellMeta.reg_cend -> generalFormat+" (reg: cend)"
//        CellMeta.reg_dend -> generalFormat+" (reg: dend)"
        }
    }
}
