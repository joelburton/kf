package kf.words.core

import kf.CellMeta
import kf.ForthVM
import kf.IWordClass
import kf.Word

object wIfThen: IWordClass {
    override val name = "IfThen"
    override val description = "Conditionals"

    override val words
        get() = arrayOf(
            Word("IF", ::w_if, imm = true, compO = true),
            Word("ELSE", ::w_else, imm = true, compO = true),
            Word("THEN", ::w_then, imm = true, compO = true),
        )

    /** IF   CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * ( C: -- orig )
     * Put the location of a new unresolved forward reference orig onto the
     * control flow stack. Append the run-time semantics given below to the
     * current definition. The semantics are incomplete until orig is resolved,
     * e.g., by THEN or ELSE.
     *
     * Run-time:
     * ( x -- )
     * If all bits of x are zero, continue execution at the location specified by the resolution of orig.
     */

    fun w_if(vm: ForthVM) {
        vm.appendWord("0branch")
        vm.dstk.push(vm.cend)
        vm.appendCode(0xffff, CellMeta.JumpLoc)
    }

    /** ELSE     CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * ( C: orig1 -- orig2 )
     * Put the location of a new unresolved forward reference orig2 onto the
     * control flow stack. Append the run-time semantics given below to the
     * current definition. The semantics will be incomplete until orig2 is
     * resolved (e.g., by THEN). Resolve the forward reference orig1 using the
     * location following the appended run-time semantics.
     *
     * Run-time:
     * ( -- )
     * Continue execution at the location given by the resolution of orig2.
     */

    fun w_else(vm: ForthVM) {
        val ifRef = vm.rstk.pop()
        vm.appendWord("branch")
        vm.dstk.push(vm.cend)
        vm.appendCode(0xfffe, CellMeta.JumpLoc)
        vm.mem[ifRef] = (vm.cend - ifRef)
    }

    /** THEN     CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * ( C: orig -- )
     * Append the run-time semantics given below to the current definition.
     * Resolve the forward reference orig using the location of the appended
     * run-time semantics.
     *
     * Run-time:
     * ( -- )
     * Continue execution.
     */

    fun w_then(vm: ForthVM) {
        val ifRef: Int = vm.dstk.pop()
        vm.mem[ifRef] = vm.cend - ifRef
    }
}