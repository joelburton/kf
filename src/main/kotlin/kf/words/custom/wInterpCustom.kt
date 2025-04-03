package kf.words.custom

import kf.IntQuitNonInteractive
import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWord

/** The interpreter primitives.
 *
 * These are the primitives required for the interpreter, as well as some
 * things about some words (like `include` for reading Forth files in) that
 * are strongly related to the interpreter. */
object wInterpCustom : IWordModule {
    override val name = "kf.words.custom.wInterpCustom"
    override val description = "Old interpreter primitives"
    override val words
        get() = arrayOf<IWord>(
            Word("INTERP-PROMPT", ::w_interpPrompt),
            Word("INTERP-OK", ::w_interpOk),

            // exiting the interpreter
            Word("\\\\\\", ::w_tripleBackSlash),
            Word("EOF", ::w_eof),

            // useful words for working with interpreter
            Word("INTERP-RELOAD-CODE", ::w_interpReloadCode),
        )

    // ********************************************** words for interpreter loop

    /** `interp-prompt` ( -- : show prompt for interpreter ) */
    fun w_interpPrompt(vm: IForthVM) {
        if (vm.verbosity >= -1 && vm.source.id == 0) {
            val stkLen: Int = vm.dstk.size
            if (vm.interp.isInterpreting) {
                vm.io.print("($stkLen) >>> ")
            } else {
                vm.io.print("($stkLen) ... ")
            }
        }
    }


    // ************************************************************* exit interp
    /**  `\\\` ( -- : stop reading file, proceed to next or to console )
     *
     * This doesn't do anything useful when already in console io (though it
     * does push 0 to stack, because that's what GForth does :) ). */
    private fun w_tripleBackSlash(vm: IForthVM) {
        if (vm.source.id > 0) {
            vm.source.pop()
            if (vm.sources.isEmpty()) throw IntQuitNonInteractive()
        } else {
            vm.dstk.push(vm.source.id)
        }
    }

    /**  `eof` ( -- : stop reading file, proceed to next or exit interp )
     *
     * In the console, this exits the interp (it's what Control-D does in
     * the interp). When reading a file, it stops reading that file, and
     * moves to the next (exiting if there are no more). */
    private fun w_eof(vm: IForthVM) {
        vm.source.pop()
    }


    // ************************************************************ useful tools

    /**  `[` ( -- : enter immediate mode immediately )
     *
     * This is useful when writing a definition and want some immediate
     * behavior during the compilation. It's also handy when hacking around
     * with interpreter state and need to return to immediate mode. */


    /** ``']'`` ( -- : enter compile mode )
     *
     * This is generally used when you were in compile mode, in the middle of
     * a word definition creation, and needed to do something immediate, and
     * now want to return to compiling mode. A simple example would be:
     *
     * ```: test 10 [65 emit] 20 ;```
     *
     * This creates a function that, when run, will put 10 and 20 on the stack,
     * but when *first compiled*, will print 'A'. */


    /** `interp-reload-code` ( -- : reloads orig interp code at cend )
     *
     * This loads the original interpreter loop code to the code section.
     * This is useful to hack on the interpreter without having to change the
     * real one --- if something is wrong with the new one, a `reset` will
     * state running back at code state, running the original interpreter. */
    private fun w_interpReloadCode(vm: IForthVM) {
        vm.interp.addInterpreterCode()
    }


    fun w_interpOk(vm: IForthVM) {
        if (vm.verbosity >= 0)
            vm.io.ok("  ok${if (vm.dstk.size > 0) "-" + vm.dstk.size else ""}")
    }
}