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
import kf.IWordClass
import kf.TerminalFileInterface
import kf.TerminalStringInterface
import kf.VERSION_STRING
import kf.Word
import kf.WordClass
import kf.words.wInterp

//import kf.words.wMachineCustom.addInterpreterCode


/** The interpreter primitives.
 *
 * These are the primitives required for the interpreter, as well as some
 * things about some words (like `include` for reading Forth files in) that
 * are strongly related to the interpreter. */
object WInterp : IWordClass {
    override val name = "Interp"
    override val description = "Old interpreter primitives"
    override val words
        get() = arrayOf(
            // the interpreter loop (plus stuff in Machine)
            Word("interp-prompt", ::w_interpPrompt),
            Word("interp-read", ::w_interpRead),
//            Word("interp-process", ::w_interpProcess),

            // exiting the interpreter
            Word("\\\\\\", ::w_tripleBackSlash),
            Word("eof", ::w_eof),

            // useful words for working with interpreter
            Word("interp-reload-code", ::w_interpReloadCode),

//            Word("parse-name", ::w_parseName),
            Word("eval", ::w_eval),
            Word("banner", ::w_banner),
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


    /**  `interp-read` ( in:"word" -- len : read next token ) */
    fun w_interpRead(vm: ForthVM) {
        try {
            val s = vm.getToken()
            vm.dstk.push(s.length)
        } catch (`_`: ForthMissingToken) {
            vm.dstk.push(0)
        }
    }

//    /**  `interp-process` ( -- : process token: compile/exec, dep on state ) */
//    fun w_interpProcess(vm: ForthVM) {
//        val token: String = vm.interpToken
//        if (vm.isCompilingState) wInterp.compile(vm, token)
//        else wInterp.interpret(vm, token)
//    }


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


    /**  'interp-reload-code` ( -- : reloads orig interp code at cend )
     *
     * This loads the original interpreter loop code to the code section.
     * This is useful to hack on the interpreter without having to change the
     * real one --- if something is wrong with the new one, a `reset` will
     * state running back at code state, running the original interpreter. */
    private fun w_interpReloadCode(vm: ForthVM) {
//        addInterpreterCode(vm)   FIXME
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