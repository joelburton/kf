package kf.primitives

import kf.CellMeta
import kf.ForthVM
import kf.Word
import kf.WordClass

object WIfThen : WordClass {
    override val name = "IfThen"

    override val primitives: Array<Word> = arrayOf(
        Word("if", ::w_if, imm = true, compO = true),
        Word("else", ::w_else, imm = true, compO = true),
        Word("then", ::w_then, imm = true, compO = true),
    )

    /**  if I ( f -- : if truthy, execute section )
     */
    fun w_if(vm: ForthVM) {
        vm.appendWord("0branch")
        vm.dstk.push(vm.cend)
        vm.appendCode(0xff, CellMeta.JumpLoc)
    }

    /**  else */
    fun w_else(vm: ForthVM) {
        val orig: Int = vm.dstk.pop()
        vm.appendWord("branch")
        vm.dstk.push(vm.cend)
        vm.appendCode(0xff, CellMeta.JumpLoc)
        vm.mem[orig] = vm.cend
    }

    /**  _then */
    fun w_then(vm: ForthVM) {
        val orig: Int = vm.dstk.pop()
        vm.mem[orig] = vm.cend
    }
}