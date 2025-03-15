@file:Suppress("unused")

package kf.primitives

import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.success
import kf.D
import kf.ForthEOF
import kf.ForthMissingToken
import kf.ForthQuitNonInteractive
import kf.ForthVM
import kf.TerminalFileInterface
import kf.TerminalStringInterface
import kf.VERSION_STRING
import kf.Word
import kf.WordClass


/** The interpreter primitives.
 *
 * These are the primitives required for the interpreter, as well as some
 * things about some words (like `include` for reading Forth files in) that
 * are strongly related to the interpreter. */
object WInterp: WordClass {
    override val name = "Interp"

    override val primitives: Array<Word> = arrayOf(
        // the interpreter loop (plus stuff in Machine)
        Word("interp-prompt", ::w_interpPrompt ) ,
        Word("interp-refill", ::w_interpRefill ) ,
        Word("interp-read", ::w_interpRead ) ,
        Word("interp-process", ::w_interpProcess ) ,

        // exiting the interpreter
        Word("\\\\\\", ::w_tripleBackSlash ) ,
        Word("eof", ::w_eof ) ,

        // useful words for working with interpreter
        Word("interp-reload-code", ::w_interpReloadCode ) ,
        Word("[", ::w_goImmediate , imm = true, compO = true) ,
        Word("]", ::w_goCompiled , imm = true) ,

        Word("parse-name", ::w_parseName ) ,
        Word("find", ::w_find ) ,
        Word("eval", ::w_eval ) ,
        Word("banner", ::w_banner ) ,
    )

    // ********************************************** words for interpreter loop

    /** `interp-prompt` ( -- : show prompt for interpreter ) */
    fun w_interpPrompt(vm: ForthVM) {
        if (vm.verbosity >= -1) {
            val stkLen: Int = vm.dstk.size
            if (vm.isInterpretingState) {
                vm.io.print(bold(green("($stkLen) >>> ")))
            } else {
                vm.io.print(bold(green("($stkLen) ... ")))
            }
        }
    }

    /**  `interp-refill` ( -- : read line from io and set up scanner )
     *
     * This pushes 0 for eof-detected, and 1 otherwise */
    fun w_interpRefill(vm: ForthVM) {
        val input = vm.io.readLineOrNull(false)
        if (input == null) {
            vm.dstk.push(0)
        } else {
            vm.interpScanner.fill(input)
            vm.dstk.push(1)
        }
    }

    /**  `interp-read` ( in:"word" -- len : read next token ) */
    fun w_interpRead(vm: ForthVM) {
        try {
            val s = vm.getToken()
            vm.dstk.push(s.length)
        } catch (`_`: ForthMissingToken) {
            vm.dstk.push(0)
        }
    }

    /**  `interp-process` ( -- : process token: compile/exec, dep on state ) */
    fun w_interpProcess(vm: ForthVM) {
        val token: String = vm.interpToken
        if (vm.isCompilingState) vm.interpCompile(token)
        else vm.interpInterpret(token)
    }


    // ************************************************************* exit interp
    /**  `\\\` ( -- : stop reading file, proceed to next or to console )
     *
     * This doesn't do anything useful when already in console io (though it
     * does push 0 to stack, because that's what GForth does :) ). */
    private fun w_tripleBackSlash(vm: ForthVM) {
        if (vm.io.terminalInterface is TerminalFileInterface) {
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
    private fun w_eof(vm: ForthVM) {
        throw ForthEOF()
    }



    // ************************************************************ useful tools

    /**  `[` ( -- : enter immediate mode immediately )
     *
     * This is useful when writing a definition and want some immediate
     * behavior during the compilation. It's also handy when hacking around
     * with interpreter state and need to return to immediate mode. */
    fun w_goImmediate(vm: ForthVM) {
        vm.interpState = ForthVM.Companion.INTERP_STATE_INTERPRETING
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
    fun w_goCompiled(vm: ForthVM) {
        vm.interpState = ForthVM.Companion.INTERP_STATE_COMPILING
    }

    /**  'interp-reload-code` ( -- : reloads orig interp code at cend )
     *
     * This loads the original interpreter loop code to the code section.
     * This is useful to hack on the interpreter without having to change the
     * real one --- if something is wrong with the new one, a `reset` will
     * state running back at code state, running the original interpreter. */
    private fun w_interpReloadCode(vm: ForthVM) {
        vm.addInterpreterCode()
    }

    /** `parse-name` `( "name" -- addr u : get token from input )` */

    private fun w_parseName(vm: ForthVM) {
        if (D) vm.dbg(2, "w_parseName")
        val (addr, len) = vm.interpScanner.parseName()
        vm.dstk.push(addr, len)
    }

    /** `find` `( addr u -- addr 0 | xt 1 | xt -1 : find word: 1=imm, -1=not-imm )` */

    private fun w_find(vm: ForthVM) {
        if (D) vm.dbg(2, "w_find")
        val len: Int = vm.dstk.pop()
        val addr: Int = vm.dstk.pop()
        val w = vm.dict.getSafe(vm.interpScanner.getAsString(addr, len))
        if (w == null) {
            if (D) vm.dbg(3, "w_find: not found")
            vm.dstk.push(addr, 0)
        } else if (w.imm) {
            if (D) vm.dbg(3, "w_find: immediate")
            vm.dstk.push(w.wn, 1)
        } else {
            if (D) vm.dbg(3, "w_find: not immediate")
            vm.dstk.push(w.wn, -1)
        }
    }

    /**`eval` ( addr u -- : evaluate string of Forth ) */

    private fun w_eval(vm: ForthVM) {
        // TODO: this might not be the best approach; we'd want
        // everything the same: ANSI, raw-term-ability, etc
        // better perhaps: being able to "stuff" input into
        // the normal input?
        // or, even better: a different string buffer loc

        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        val s = vm.interpScanner.getAsString(addr, len)

        val prevIO = vm.io
        val prevVerbosity = vm.verbosity

        vm.io = Terminal(terminalInterface = TerminalStringInterface(s))
        vm.verbosity = -2

        try {
            vm.runVM()
        } catch (_: ForthQuitNonInteractive) {

        } catch (_: ForthEOF) {

        } finally {
            vm.io = prevIO
            vm.verbosity = prevVerbosity
        }
    }

    /** `banner` `( -- : print welcome banner )` */

    fun w_banner(vm: ForthVM) {
        if (vm.verbosity > 0) vm.io.success("\nWelcome to ${VERSION_STRING}\n")
    }
}