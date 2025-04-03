package kf.words.core

import kf.dict.NO_ADDR
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.mem.CellMeta
import kf.mem.appendCode

object wIfThen: IWordModule {
    override val name = "kf.words.core.wIfThen"
    override val description = "Conditionals"

    override val words
        get() = arrayOf<Word>(
            Word("IF", ::w_if, imm = true, compO = true),
            Word("ELSE", ::w_else, imm = true, compO = true),
            Word("THEN", ::w_then, imm = true, compO = true),
        )

    /** `IF` IM CO ( f -- ) If flag is false, skip to ELSE (or THEN) */

    fun w_if(vm: IForthVM) {
        vm.appendWord("0branch")
        vm.dstk.push(vm.cend)
        // "THEN" will fix this fake address
        vm.appendCode(NO_ADDR, CellMeta.JumpLoc)
    }

    /** `ELSE` IM CO ( -- ) If provided, if IF fails, start exec here */

    fun w_else(vm: IForthVM) {
        val ifRef = vm.dstk.pop()
        vm.appendWord("branch")
        vm.dstk.push(vm.cend)
        // "THEN" will fix this fake address
        vm.appendCode(0xfffe, CellMeta.JumpLoc)
        // but we can fix the "IF" fake fwd-ref
        vm.mem[ifRef] = (vm.cend - ifRef)
    }

    /** THEN ( -- ) Finish the IF/ELSE/THEN */

    fun w_then(vm: IForthVM) {
        val ifRef: Int = vm.dstk.pop()
        // Fix the fake ref of ELSE (or if no ELSE, the one for IF)
        vm.mem[ifRef] = vm.cend - ifRef
    }
}
