package kf

class WIfThen(val vm: ForthVM) : WordClass {
    override val name = "IfThen"

    override val primitives: Array<Word> = arrayOf<Word>(
        Word("if", imm = true, compO = true) { _ -> w_if() },
        Word("else", imm=true, compO = true ) { _-> w_else() },
        Word("then", imm=true, compO = true) { _ -> w_then() },
    )

    /**  _if */
    fun w_if() {
        vm.appendWord("0branch")
        vm.dstk.push(vm.cend)
        vm.appendCode(0xff, CellMeta.jump_location)
    }

    /**  else */
    fun w_else() {
        val orig: Int = vm.dstk.pop()
        vm.appendWord("branch")
        vm.dstk.push(vm.cend)
        vm.appendCode(0xff, CellMeta.jump_location)
        vm.mem[orig] = vm.cend
    }

    /**  _then */
    fun w_then() {
        val orig: Int = vm.dstk.pop()
        vm.mem[orig] = vm.cend
    }
}