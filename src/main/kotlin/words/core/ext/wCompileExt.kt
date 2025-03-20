package kf.words.core.ext

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.terminal.warning
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl

object wCompileExt: IWordClass {
    override val name = "core.ext.compileExt"
    override val description = "Compile Extensions"
    override val words = arrayOf<Word>(
        Word("[COMPILE]", ::w_bracketCompile, imm = true, compO = true),
        Word("COMPILE,", ::w_compileComma, compO = true),
        Word(".(", ::w_notImpl),
        Word("NONAME:", ::w_notImpl)
        )


    /** Compile a word (used by `compile,` and ```[compile]```, below.) */

    fun compile(vm: ForthVM, wn: Int) {
        val w = vm.dict[wn]

        if (vm.interp.isInterpreting) {
            vm.io.warning("Interpreting a postponed non-immediate word: '$w'."
                    + " This is probably not what you want to do."
                    + " (Use 'immediate' to mark the word as immediate-mode.)",
                whitespace = Whitespace.NORMAL,
                overflowWrap = OverflowWrap.NORMAL)
            vm.interp._interpret(vm.dict[wn].name)
        }
        vm.interp._compile(w.name)
    }

    /** ```[bracket]``` ( "name" -- compile this word ) */

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


}