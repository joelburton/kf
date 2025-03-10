package kf

import java.io.FileNotFoundException
import java.util.Scanner

/** The interpreter primitives.
 *
 * These are the primitives required for the interpreter, as well as some
 * things about some words (like `include` for reading Forth files in) that
 * are strongly related to the interpreter. */
class WInterp(val vm: ForthVM) {
    val primitives: Array<Word> = arrayOf<Word>(
        // the interpreter loop (plus stuff in Machine)
        Word("interp-prompt") { _-> w_interpPrompt() },
        Word("interp-refill") { _ -> w_interpRefill() },
        Word("interp-read") { _ -> w_interpRead() },
        Word("interp-process") { _ -> w_interpProcess() },

        // exiting the interpreter
        Word("\\\\\\") { _ -> w_tripleBackSlash() },
        Word("eof") { _ -> w_eof() },

        // including new primitives and forth files
        Word("include") { _ -> w_include() },
        Word("include-primitives") { _ -> w_includeBinary() },

        // useful words for working with interpreter
        Word("state") { _ -> w_stateReg() },
        Word("interp-reload-code") { _ -> w_interpReloadCode() },
        Word("[", immediate = true, compileOnly = true) { _ -> w_goImmediate() },
        Word("]", immediate=true) { _ -> w_goCompiled() },
    )

    // ********************************************** words for interpreter loop

    /** `interp-prompt` ( -- : show prompt for interpreter ) */
    fun w_interpPrompt() {
        if (D) vm.dbg(3, "w_interPrompt")
        if (vm.verbosity >= -1) {
            val stk_len: Int = vm.dstk.size
            if (vm.isInterpretingState) {
                vm.io.output.print(vm.io.green("($stk_len) >>> "))
            } else {
                vm.io.output.print(vm.io.green("($stk_len) ... "))
            }
        }
    }

    /**  `interp-refill` ( -- : read line from io and set up scanner )
     *
     * This pushes 0 for eof-detected, and 1 otherwise */
    fun w_interpRefill() {
        if (D) vm.dbg(3, "w_interpRefill")
        vm.interpLineBuf = vm.io.readLine()
        if (vm.interpScanner != null) {
            vm.interpScanner!!.close()
            vm.interpScanner = null
        }
        if (vm.interpLineBuf == null) {
            vm.dstk.push(0)
            return
        }
        vm.interpScanner = Scanner(vm.interpLineBuf)
        vm.dstk.push(1)
    }

    /**  `interp-read` ( in:"word" -- len : read next token ) */
    fun w_interpRead() {
        if (D) vm.dbg(3, "w_interpRead")
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
        if (D) vm.dbg(3, "w_interpProcess: \"%s\"", token)
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
        if (D) vm.dbg("w_tripleBackSlash")
        if (vm.io is IOFile) {
            throw ForthVM.ForthQuitNonInteractive()
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
        if (D) vm.dbg("w_eof")
        throw ForthVM.ForthEOF()
    }


    // ************************************************************
    /**  `include` ( in:"file" -- : read Forth file in ) */
    fun w_include() {
        val path = vm.getToken()
        if (D) vm.dbg("w_include: '%s'", path)

        val prevIO: IOBase = vm.io
        val prevVerbosity: Int = vm.verbosity

        try {
            vm.io = IOFile(path)
        } catch (e: FileNotFoundException) {
            throw ForthError("No such file: $path")
        }

        try {
            vm.runVM()
        } catch (e: ForthVM.ForthQuitNonInteractive) {
            // Caused by the EOF or \\\ commands --- stop reading this file, but
            // not an error --- will proceed to next file or to console
        } catch (_: ForthVM.ForthEOF) {
        } finally {
            vm.io = prevIO
            vm.verbosity = prevVerbosity
        }
    }

    /**  `include-primitives` ( in:"file" -- : Read class file of primitives )
     *
     * These can be anything the JVM can understand: Java, Kotlin, Groovy, etc. */
    fun w_includeBinary() {
        val path = vm.getToken()
        if (D) vm.dbg("w_includeBinary: '%s'", path)
        vm.readPrimitiveClass(path)
    }

    // ************************************************************ useful tools
    /**  `state` ( -- addr : gets address of interpreter-state register )
     *
     * This is very internal, but useful for writing Forth code for compiling
     * words, writing new control words, etc. */
    private fun w_stateReg() {
        if (D) vm.dbg("w_stateReg")
        vm.dstk.push(vm.REG_INTERP_STATE)
    }

    /**  `[` ( -- : enter immediate mode immediately )
     *
     * This is useful when writing a definition and want some immediate
     * behavior during the compilation. It's also handy when hacking around
     * with interpreter state and need to return to immediate mode. */
    fun w_goImmediate() {
        if (D) vm.dbg("w_goImmediate")
        vm.interpState = vm.INTERP_STATE_INTERPRETING
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
        if (D) vm.dbg("w_goCompiled")
        vm.interpState = vm.INTERP_STATE_COMPILING
    }

    /**  'interp-reload-code` ( -- : reloads orig interp code at cend )
     *
     * This loads the original interpreter loop code to the code section.
     * This is useful to hack on the interpreter without having to change the
     * real one --- if something is wrong with the new one, a `reset` will
     * state running back at code state, running the original interpreter. */
    private fun w_interpReloadCode() {
        if (D) vm.dbg("w_interpReloadCode")
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