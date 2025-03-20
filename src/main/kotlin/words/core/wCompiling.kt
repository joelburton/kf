package kf.words.core

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.terminal.warning
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.interps.InterpBase

object wCompiling: IWordClass {
    override val name = "Compiling"
    override val description = "Compiling colon words"

    override val words
        get() = arrayOf(
            Word(":", ::w_colon, interpO = true),
            Word(";", ::w_semicolon, imm = true, compO = true),
            Word("IMMEDIATE", ::w_immediate, imm = true),
            Word("RECURSE", ::w_recurse, imm = true),
            Word("POSTPONE", ::w_postpone, imm = true, compO = true),
            Word("LITERAL", ::w_literal, imm = true, compO = true),

            // non-standard
            Word(";S", wFunctions::w_exit)
        )

    /** :    colon   CORE
     *
     * ( C: "<spaces>name" -- colon-sys )
     *
     * Skip leading space delimiters. Parse name delimited by a space. Create
     * a definition for name, called a "colon definition". Enter compilation
     * state and start the current definition, producing colon-sys. Append the
     * initiation semantics given below to the current definition.
     *
     * The execution semantics of name will be determined by the words compiled
     * into the body of the definition. The current definition shall not be
     * findable in the dictionary until it is ended (or until the execution of
     * DOES> in some systems).
     *
     */

    fun w_colon(vm: ForthVM) {
        val name: String = vm.interp.getToken()

        // Words start off not-recursive hidden, so they can't call themselves
        // while still being compiled. This allows:
        //
        //   : a 65 emit ;
        //   : a a ;         <- new `a` is calling previous a
        //
        // Making the function recursive (`recursive`) will mark the
        // currently-being-defined word as such while being compiled,
        // so it can call itself (recurse).

        val w = Word(
            name,
            cpos = vm.cend,
            dpos = Word.NO_ADDR,
            fn = wFunctions::w_call
        )
        vm.dict.add(w)
        vm.interp.state = InterpBase.STATE_COMPILING
        vm.dict.currentlyDefining = w
    }

    /** ;    semicolon   CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * ( C: colon-sys -- )
     * Append the run-time semantics below to the current definition. End the current definition, allow it to be found in the dictionary and enter interpretation state, consuming colon-sys. If the data-space pointer is not aligned, reserve enough data space to align it.
     *
     * Run-time:
     * ( -- ) ( R: nest-sys -- )
     * Return to the calling definition specified by nest-sys.
     */

    fun w_semicolon(vm: ForthVM) {
        val w = vm.dict.last
        vm.appendWord(";s")
        vm.dict.currentlyDefining = null
        vm.interp.state = InterpBase.STATE_INTERPRETING
    }

    /** IMMEDIATE    CORE
     *
     * ( -- )
     *
     * Make the most recent definition an immediate word. An ambiguous
     * condition exists if the most recent definition does not have a name or
     * if it was defined as a SYNONYM.
     *
     *      * Warns about using immediate like this:
     *      * : a 65 emit ; immediate
     *      * Doesn't warn for usage like this:
     *      * : a 65 emit immediate ;
     *      *
     *      * (since it can be a little bug-prone to mark-most-recent-as-immediate
     *      * when the user may have forgotten there was another function defined
     *      * between thw one they wanted to make immediate)
     */

    fun w_immediate(vm: ForthVM) {
        val w = vm.dict.last
        w.imm = true
        if (vm.dict.currentlyDefining == null) {
            vm.dbg(
                0, """Marked '$w' as immediate-mode
(Put "immediate" inside definition to avoid warning)"""
            )
        }
    }

    /** RECURSE  CORE
     *
     * Interpretation:
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     * ( -- )
     * Append the execution semantics of the current definition to the current
     * definition. An ambiguous condition exists if RECURSE appears in a
     * definition after DOES>.
     */

    fun w_recurse(vm: ForthVM) {
        vm.appendWord(vm.dict.last.name)
    }

    /**
     * POSTPONE  CORE
     *
     * Interpretation:
     *
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     *
     * ( "<spaces>name" -- )
     *
     * Skip leading space delimiters. Parse name delimited by a space. Find
     * name. Append the compilation semantics of name to the current
     * definition. An ambiguous condition exists if name is not found.
     *
     *
     *      * Postpone is useful for postponing-evaluating a word:
     *      *
     *      * For example:
     *      *   : aa 'a' ;
     *      *   : x immediate postpone aa ;
     *      *   : y x x ;
     *      *
     *      *  'y' is compiled to "aa / aa / ret" (not: "x / x / ret").
     *      *
     *      *  It can also be used to alias a word:
     *      *    : my-if immediate postpone if ;
     *      *    : test 10 my-if 20 then ;
     *      *
     *      * Which compiles to the same thing as if "test" used "if" directly.
     *
     *
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

    /** LITERAL  CORE
     *
     * Interpretation:
     *
     * Interpretation semantics for this word are undefined.
     *
     * Compilation:
     *
     *  ( x -- )
     *
     * Append the run-time semantics given below to the current definition.
     *
     * Run-time:
     *
     *  ( -- x )
     *
     * Place x on the stack.
     */

    fun w_literal(vm: ForthVM) {
        val v: Int = vm.dstk.pop()
        vm.appendLit(v)
    }
}