package kf.words.tools

import kf.IntBye
import kf.dict.NO_ADDR
import kf.dict.Word
import kf.dict.w_notImpl
import kf.interfaces.FALSE
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.interfaces.TRUE
import kf.strFromAddrLen

object wToolsExt: IWordModule {
    override val name = "kf.words.tools.wToolsExt"
    override val description = "Programming tools"

    override val words
        get() = arrayOf<Word>(
            Word("AHEAD", ::w_notImpl),
            Word("ASSEMBLER", ::w_notImpl),
            Word("BYE", ::w_bye),
            Word("[DEFINED]", ::w_bracketDefined, imm = true),
            Word("[ELSE]", ::w_notImpl),
            Word("[IF]", ::w_notImpl),
            Word("[THEN]", ::w_notImpl),
            Word("[UNDEFINED]", ::w_bracketUndefined, imm = true),
            Word("CODE", ::w_notImpl),
            Word("CS-PICK", ::w_notImpl),
            Word("CS-ROLL", ::w_notImpl),
            Word("EDITOR", ::w_notImpl),
            Word("FORGET", ::w_forget),
            Word("NAME>COMPILE", ::w_notImpl),
            Word("NAME>INTERPRET", ::w_notImpl),
            Word("NAME>STRING", ::w_notImpl),
            Word("NR>", ::w_notImpl),
            Word("N>R", ::w_notImpl),
            Word("SYNONYM", ::w_synonym),
            Word(";CODE", ::w_notImpl),
            Word("TRAVERSE-WORDLIST", ::w_notImpl),
        )


    /**  `\[defined\]` I ( in:"name" -- f : is this word defined? )
     */
    private fun w_bracketDefined(vm: IForthVM) {
        val token: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val def = vm.dict.getSafe(token) != null
        vm.dstk.push(if (def) TRUE else FALSE)
    }

    /**  `\[undefined\]` I ( in:"name" -- f : is this word undefined? )
     */
    private fun w_bracketUndefined(vm: IForthVM) {
        val token: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val def = vm.dict.getSafe(token) != null
        vm.dstk.push(if (def) FALSE else TRUE)
    }

    // newname old-name
    /** `synonym` ( in:"new" in:"old" -- : makes new word as alias of old )
     */
    fun w_synonym(vm: IForthVM) {
        val newName: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val oldName: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val curWord = vm.dict[oldName]
        val nw = Word(
            newName,
            fn = curWord.fn,
            cpos = curWord.cpos,
            dpos = curWord.dpos,
            compO = curWord.compO,
            imm = curWord.imm,
            interpO = curWord.interpO,
        )
        vm.dict.add(nw)
    }

    /**  `forget` ( in:"name" -- : delete word and all following words )
     */
    fun w_forget(vm: IForthVM) {
        val newName: String =  vm.source.scanner.parseName().strFromAddrLen(vm)
        val w = vm.dict[newName]
        vm.dict.truncateAt(w.wn)
        if (w.cpos != NO_ADDR) vm.cend = w.cpos
    }


    /** `bye` ( -- Quit interpreter )
     *
     * Quits the interpreter. If using the interpreter directly, this will end
     * up quitting the entire program. If the interpreter is running an
     * interpreter (like when using `include` to read in additional Forth
     * files), this will quit the inner interpreter and return to the outer
     * one.
     *
     * When running as a long-running gateway to a VM, this is caught and
     * just reboots the machine, starting a new interpreter.
     */

    fun w_bye(vm: IForthVM) {
        throw IntBye()
    }

}