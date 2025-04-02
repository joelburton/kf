package kf.words.core.ext

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IWord
import kf.interps.InterpBase
import kf.strFromAddrLen
import kf.words.core.wFunctions

object wCompileExt: IWordModule {
    override val name = "kf.words.core.ext.wCompileExt"
    override val description = "Compile Extensions"
    override val words = arrayOf<IWord>(
        Word("[COMPILE]", ::w_bracketCompile, imm = true, compO = true),
        Word("COMPILE,", ::w_compileComma, compO = true),
        Word(".(", ::w_dotParen, imm=true, compO = true),
        Word(":NONAME", ::w_colonNoName, imm = true),
        )


    /** Compile a word (used by `compile,` and ```[compile]```, below.) */

    private fun compile(vm: ForthVM, wn: Int) {
        val w = vm.dict[wn]

        if (vm.interp.isInterpreting) {
            vm.io.warning("Interpreting a postponed non-immediate word: '$w'.")
            vm.io.warning(" This is probably not what you want to do.")
            vm.io.warning(" Use '.(' to compile a postponed non-immediate word.")
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

    // fixme: i think this is wrong -- real compile, is just "," ---
    //   doesn't check imm mode, etc

    fun w_compileComma(vm: ForthVM) {
        compile(vm, vm.dstk.pop())
    }

    /** `.(` IM CO ( -- ) Print message immediately */

    fun w_dotParen(vm: ForthVM) {
        var msg = vm.source.scanner.parse(')').strFromAddrLen(vm)
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