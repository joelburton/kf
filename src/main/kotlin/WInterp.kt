package kf

import java.util.Scanner
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextStyles.bold


/** The interpreter primitives.
 *
 * These are the primitives required for the interpreter, as well as some
 * things about some words (like `include` for reading Forth files in) that
 * are strongly related to the interpreter. */
class WInterp(val vm: ForthVM): WordClass {
    override val name = "Interp"

    override val primitives: Array<Word> = arrayOf(
        // the interpreter loop (plus stuff in Machine)
        Word("interp-prompt") { w_interpPrompt() },
        Word("interp-refill") { w_interpRefill() },
        Word("interp-read") { w_interpRead() },
        Word("interp-process") { w_interpProcess() },

        // exiting the interpreter
        Word("\\\\\\") { w_tripleBackSlash() },
        Word("eof") { w_eof() },

        // useful words for working with interpreter
        Word("interp-reload-code") { w_interpReloadCode() },
        Word("[", imm = true, compO = true) { w_goImmediate() },
        Word("]", imm=true) { w_goCompiled() },
    )

    // ********************************************** words for interpreter loop

    /** `interp-prompt` ( -- : show prompt for interpreter ) */
    fun w_interpPrompt() {
        if (vm.verbosity >= -1) {
            val stk_len: Int = vm.dstk.size
            if (vm.isInterpretingState) {
                vm.io.print(bold(green("($stk_len) >>> ")))
            } else {
                vm.io.print(bold(green("($stk_len) ... ")))
            }
        }
    }

    /**  `interp-refill` ( -- : read line from io and set up scanner )
     *
     * This pushes 0 for eof-detected, and 1 otherwise */
    fun w_interpRefill() {
        vm.interpLineBuf = vm.io.readLineOrNull(false)

        if (vm.interpScanner != null) {
            vm.interpScanner!!.close()
            vm.interpScanner = null
        }

        if (vm.interpLineBuf == null) {
            vm.dstk.push(0)
        } else {
            vm.interpScanner = Scanner(vm.interpLineBuf!!)
            vm.dstk.push(1)
        }
    }

    /**  `interp-read` ( in:"word" -- len : read next token ) */
    fun w_interpRead() {
        try {
            val s = vm.getToken()
            vm.dstk.push(s.length)
        } catch (`_`: ForthMissingToken) {
            vm.dstk.push(0)
        }
    }

    /**  `interp-process` ( -- : process token: compile/execute, dep on state ) */
    fun w_interpProcess() {
        val token: String = vm.interpToken!!
        if (token.isEmpty()) return  // FIXME: look into this -- could this even happen?

        if (vm.isCompilingState) vm.interpCompile(token)
        else vm.interpInterpret(token)
    }


    // ************************************************************* exit interp
    /**  `\\\` ( -- : stop reading file, proceed to next or to console )
     *
     * This doesn't do anything useful when already in console io (though it
     * does push 0 to stack, because that's what GForth does :) ). */
    private fun w_tripleBackSlash() {
        if (vm.io is IOFile) {
            throw ForthQuitNonInteractive()
        } else {
            vm.dstk.push(0)
        }
    }

    /**  `eof` ( -- : stop reading file, proceed to next or exit interp )
     *
     * In the console, this exits the interp (it's what Control-D does in
     * the interp). When reading a file, it stops reading that file, and
     * moves to the next (exiting if there are no more). */
    private fun w_eof() {
        throw ForthEOF()
    }



    // ************************************************************ useful tools

    /**  `[` ( -- : enter immediate mode immediately )
     *
     * This is useful when writing a definition and want some immediate
     * behavior during the compilation. It's also handy when hacking around
     * with interpreter state and need to return to immediate mode. */
    fun w_goImmediate() {
        vm.interpState = ForthVM.INTERP_STATE_INTERPRETING
    }

    /** ']' ( -- : enter compile mode )
     *
     * This is generally used when you were in compile mode, in the middle of
     * a word definition creation, and needed to do something immediate, and
     * now want to return to compiling mode. A simple example would be:
     *
     * : test 10 [65 emit] 20 ;
     *
     * This creates a function that, when run, will put 10 and 20 on the stack,
     * but when *first compiled*, will print 'A'. */
    fun w_goCompiled() {
        vm.interpState = ForthVM.INTERP_STATE_COMPILING
    }

    /**  'interp-reload-code` ( -- : reloads orig interp code at cend )
     *
     * This loads the original interpreter loop code to the code section.
     * This is useful to hack on the interpreter without having to change the
     * real one --- if something is wrong with the new one, a `reset` will
     * state running back at code state, running the original interpreter. */
    private fun w_interpReloadCode() {
        vm.addInterpreterCode(vm.cend)
    }

//    private fun w_parse() {
//        if (D) vm.dbg("w_parseName")
//        val s = getToken(vm)
//        if (s.isEmpty()) {
//            vm.dstk.push(0)
//        } else {
//            vm.dstk.push(vm.dict.getNum(s))
//        }
//    }
//
//    private fun w_find() {
//        if (D) vm.dbg("w_find")
//        val addr: Int = vm.dstk.pop()
//        val s = getToken(vm) // fixme: should take an addr!
//        val w: Word = vm.dict.getSafe(s)
//        if (w == null) {
//            if (D) vm.dbg("w_find: not found")
//            vm.dstk.push(addr, 0)
//        } else if (w.immediate) {
//            if (D) vm.dbg("w_find: immediate")
//            vm.dstk.push(addr, 1)
//        } else {
//            if (D) vm.dbg("w_find: not immediate")
//            vm.dstk.push(addr, -1)
//        }
//    }
}