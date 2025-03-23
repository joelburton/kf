package kf.words.core.ext

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.terminal.warning
import kf.ForthVM
import kf.IWordModule
import kf.Word
import kf.interps.InterpBase
import kf.strFromAddrLen
import kf.w_notImpl
import kf.words.core.wFunctions
import kf.words.core.wFunctions.w_call

object wCompileExt: IWordModule {
    override val name = "kf.words.core.ext.wCompileExt"
    override val description = "Compile Extensions"
    override val words = arrayOf<Word>(
        Word("[COMPILE]", ::w_bracketCompile, imm = true, compO = true),
        Word("COMPILE,", ::w_compileComma, compO = true),
        Word(".(", ::w_dotParen, imm=true, compO = true),
        Word(":NONAME", ::w_colonNoName, imm = true),
        )


    /** Compile a word (used by `compile,` and ```[compile]```, below.) */

    private fun compile(vm: ForthVM, wn: Int) {
        val w = vm.dict[wn]

        if (vm.interp.isInterpreting) {
            vm.io.warning("Interpreting a postponed non-immediate word: '$w'."
                    + " This is probably not what you want to do."
                    + " (Use 'immediate' to mark the word as immediate-mode.)",
                whitespace = Whitespace.NORMAL,
                overflowWrap = OverflowWrap.NORMAL)
            vm.interp.interpret(vm.dict[wn].name)
        }
        vm.interp.compile(w.name)
    }

    /** ```[compile]``` ( "name" -- compile this word ) */

    fun w_bracketCompile(vm: ForthVM) {
        compile(vm, vm.mem[vm.ip++])
    }

    /** `compile,` ( wn -- compile word number )
     *
     * This is what the interpreter loop could use.
     */

    fun w_compileComma(vm: ForthVM) {
        compile(vm, vm.dstk.pop())
    }

    /** `.(` IM CO ( -- ) Print message immediately */

    fun w_dotParen(vm: ForthVM) {
        var msg = vm.scanner.parse(')').strFromAddrLen(vm)
        vm.io.print(msg)
    }

    /** `:NONAME` ( -- xt ) Make anonymous name and put xt on stack */

    fun w_colonNoName(vm: ForthVM) {
        val newWord = Word(
            "(ANON)",
            cpos = vm.cend,
            dpos = Word.NO_ADDR,
            fn = wFunctions::w_call,
            hidden = true,
        )
        vm.dict.add(newWord)
        vm.interp.state = InterpBase.STATE_COMPILING
        vm.dict.currentlyDefining = newWord
    }
}