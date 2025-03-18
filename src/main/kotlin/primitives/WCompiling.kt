package kf.primitives

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.terminal.warning
import kf.ForthVM
import kf.Word
import kf.WordClass

object WCompiling : WordClass {
    override val name = "Compiling"

    override val primitives get() = arrayOf(
        Word("[literal]", ::w_bracketLiteral, compO = true),
        Word("recursive", ::w_recursive, imm = true, compO = true),
//        Word("dolit", ::w_doLit),
        Word("[compile]", ::w_bracketCompile, imm = true, compO = true),
        Word("compile,", ::w_compileComma, compO = true),
        Word("defer", ::w_defer),
        Word("deferred-word", ::deferred),
        Word("defer!", ::w_deferStore),
        Word("defer@", ::w_deferFetch),
        Word("is", ::w_is),
    )

    /**  `:` X ( in:"name" -- : create word 'name' and start compiling mode )
     */


    /** `;` IC ( -- : complete word definition and exit compiling mode )`
     */

    //    ///  ( -- ) really lit ? is it ok to just use lit for this?
    //    static void w_imm_lit(ForthVM vm) {
    //        String token = PrimInterpreter.getToken(vm);
    //        int val = Utils.tryAsInt(token, vm.getBase());
    //        vm.dbg("w_imm_lit %d", val);
    //        vm.appendCode(val, CellMeta.number_literal);
    //    }
    //  : [literal] dolit ,, ,, ;   \ writes lit/stack-top
    //  : literal immediate [literal] ; \ same, but imm mode
    //  : ['] immediate ' [literal] ;


    /** `recursive` CI ( -- : from this point onward, word can recurse )
     */
    fun w_recursive(vm: ForthVM) {
        val w: Word = vm.dict.currentlyDefining!!
        w.recursive = true
    }


    /**  `postpone` ( "w" -- : writes word into curr definition )
     *
     * Postpone is useful for postponing-evaluating a word:
     *
     * For example:
     *   : aa 'a' ;
     *   : x immediate postpone aa ;
     *   : y x x ;
     *
     *  'y' is compiled to "aa / aa / ret" (not: "x / x / ret").
     *
     *  It can also be used to alias a word:
     *    : my-if immediate postpone if ;
     *    : test 10 my-if 20 then ;
     *
     * Which compiles to the same thing as if "test" used "if" directly.
     */

    fun w_postpone(vm: ForthVM) {
        val token: String = vm.interp.getToken()
        val w = vm.dict[token]
        val cw = vm.dict.last

        if (!cw.imm) {
            vm.io.warning(
                """Using postpone in a word not already immediate word: '$cw'.
This is almost certainly not what you want to do.""",
                whitespace = Whitespace.NORMAL,
                overflowWrap = OverflowWrap.NORMAL)
        }
        vm.appendWord("[compile]")
        vm.appendWord(token)
    }

    /**  `bracketLiteral` C ( n -- : write lit token & n to code area )
     */
    fun w_bracketLiteral(vm: ForthVM) {
        val v: Int = vm.dstk.pop()
        vm.appendLit(v)
    }


    /** Compile a word (used by `compile,` and ```[compile]```, below.) */

    fun compile(vm: ForthVM, wn: Int) {
        val w = vm.dict[wn]

        if (vm.interp.isInterpreting) {
            vm.io.warning("Interpreting a postponed non-immediate word: '$w'."
                    + " This is probably not what you want to do."
                    + " (Use 'immediate' to mark the word as immediate-mode.)",
                whitespace = Whitespace.NORMAL,
                overflowWrap = OverflowWrap.NORMAL)
//            interpret(vm, vm.dict[wn].name) FIXME
        }
//        vm.interpCompile(w.name)   FIXME
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


    fun deferred(vm: ForthVM) {
        vm.io.warning("Use of uninitialized deferred word: ${vm.currentWord}")
    }

    /** `defer` ( "word" -- : create word pointing to uninitialized fn ) */

    fun w_defer(vm: ForthVM) {
        val name: String = vm.interp.getToken()
        val w = Word(
            name,
            cpos = Word.NO_ADDR,
            dpos = Word.NO_ADDR,
            fn = ::deferred,
            deferToWn = vm.dict["deferred-word"].wn
        )
        vm.dict.add(w)
    }

    /** `is` ( wn "word" -- : associates wn-word to "word" word ) */

    fun changeDeferPointer(vm: ForthVM, deferredWord: Word, sourceWord: Word ) {
        deferredWord.cpos = sourceWord.cpos
        deferredWord.dpos = sourceWord.dpos
        deferredWord.fn = sourceWord.fn
        deferredWord.deferToWn = sourceWord.wn
    }

    fun w_is(vm: ForthVM) {
        val sourceWord  = vm.dict[vm.dstk.pop()]
        val deferredWord = vm.dict[vm.interp.getToken()]
        changeDeferPointer(vm, deferredWord, sourceWord)
    }

    fun w_deferStore(vm: ForthVM) {
        val deferredWord = vm.dict[vm.dstk.pop()]
        val sourceWord  = vm.dict[vm.dstk.pop()]
        changeDeferPointer(vm, deferredWord, sourceWord)
    }

    fun w_deferFetch(vm: ForthVM) {
        val deferredWord = vm.dict[vm.dstk.pop()]
        vm.dstk.push(deferredWord.deferToWn ?: deferredWord.wn)
    }

}