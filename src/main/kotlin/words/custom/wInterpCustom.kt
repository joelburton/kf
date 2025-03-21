package kf.words.custom

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import kf.IntEOF
import kf.IntQuitNonInteractive
import kf.ForthVM
import kf.IWordClass
import kf.TerminalFileInterface
import kf.Word

/** The interpreter primitives.
 *
 * These are the primitives required for the interpreter, as well as some
 * things about some words (like `include` for reading Forth files in) that
 * are strongly related to the interpreter. */
object wInterpCustom : IWordClass {
    override val name = "InterpExtra"
    override val description = "Old interpreter primitives"
    override val words
        get() = arrayOf(
            Word("INTERP-PROMPT", ::w_interpPrompt),

            // exiting the interpreter
            Word("\\\\\\", ::w_tripleBackSlash),
            Word("EOF", ::w_eof),

            // useful words for working with interpreter
            Word("INTERP-RELOAD-CODE", ::w_interpReloadCode),
        )

    // ********************************************** words for interpreter loop

    /** `interp-prompt` ( -- : show prompt for interpreter ) */
    fun w_interpPrompt(vm: ForthVM) {
        if (vm.verbosity >= -1) {
            val stkLen: Int = vm.dstk.size
            if (vm.interp.isInterpreting) {
                vm.io.print(TextStyles.bold(TextColors.green("($stkLen) >>> ")))
            } else {
                vm.io.print(TextStyles.bold(TextColors.green("($stkLen) ... ")))
            }
        }
    }


    // ************************************************************* exit interp
    /**  `\\\` ( -- : stop reading file, proceed to next or to console )
     *
     * This doesn't do anything useful when already in console io (though it
     * does push 0 to stack, because that's what GForth does :) ). */
    private fun w_tripleBackSlash(vm: ForthVM) {
        if (vm.io.terminalInterface is TerminalFileInterface) {
            throw IntQuitNonInteractive()
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
        throw IntEOF()
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
        vm.interp.addInterpreterCode()
    }





    /**`eval` ( addr u -- : evaluate string of Forth ) */

//    private fun w_eval(vm: ForthVM) {
//        // TODO: this might not be the best approach; we'd want
//        // everything the same: ANSI, raw-term-ability, etc
//        // better perhaps: being able to "stuff" input into
//        // the normal input?
//        // or, even better: a different string buffer loc
//
//        val len = vm.dstk.pop()
//        val addr = vm.dstk.pop()
//        val s = vm.interp.interpScanner.getAsString(addr, len)
//
//        val prevIO = vm.io
//        val prevVerbosity = vm.verbosity
//
//        vm.io = Terminal(terminalInterface = TerminalStringInterface(s))
//        vm.verbosity = -2
//
//        try {
//            vm.runVM()
//        } catch (_: ForthQuitNonInteractive) {
//
//        } catch (_: ForthEOF) {
//
//        } finally {
//            vm.io = prevIO
//            vm.verbosity = prevVerbosity
//        }
//    }

}