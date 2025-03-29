package kf.words.core

import kf.ForthVM
import kf.dict.IWordModule
import kf.dict.Word
import kf.interps.InterpBase
import kf.strFromAddrLen

object wCompiling : IWordModule {
    override val name = "kf.words.core.wCompiling"
    override val description = "Compiling colon words"

    override val words
        get() = arrayOf(
            Word(":", ::w_colon, interpO = true),
            Word(";", ::w_semicolon, imm = true, compO = true),
            Word("IMMEDIATE", ::w_immediate, imm = true),
            Word("RECURSE", ::w_recurse, imm = true, compO = true),
            Word("POSTPONE", ::w_postpone, imm = true, compO = true),
            Word("LITERAL", ::w_literal, imm = true, compO = true),

            // non-standard
            Word(";S", wFunctions::w_exit)
        )

    /** `:` IM IO ( C: "<spaces>name" -- colon-sys ) Define a new word
     *
     * The current definition shall not be findable in the dictionary until it
     * is ended (or until the execution of DOES> in some systems).
     */

    fun w_colon(vm: ForthVM) {
        val name: String = vm.source.scanner.parseName().strFromAddrLen(vm)

        val newWord = Word(
            name,
            cpos = vm.cend,
            dpos = Word.NO_ADDR,
            fn = wFunctions::w_call,
            hidden = true,
        )
        vm.dict.add(newWord)
        vm.interp.state = InterpBase.STATE_COMPILING
        vm.dict.currentlyDefining = newWord
    }

    /** `;` IM CO ( C: colon-sys -- ) Mark def as done and append exit  */

    fun w_semicolon(vm: ForthVM) {
        vm.appendWord(";S")
        if (vm.dict.currentlyDefining?.name
            .equals("(ANON)", ignoreCase = true)) {
            vm.dstk.push(vm.dict.currentlyDefining!!.wn)
        } else {
            vm.dict.last.hidden = false
        }
        vm.dict.currentlyDefining = null
        vm.interp.state = InterpBase.STATE_INTERPRETING
    }

    /** `IMMEDIATE` IM ( -- ) Make most recent def an imm word.

     *  Warns about using immediate like this:
     *  : a 65 emit ; immediate
     *  Doesn't warn for usage like this:
     *  : a 65 emit immediate ;
     *
     *  (since it can be a little bug-prone to mark-most-recent-as-immediate
     *  when the user may have forgotten there was another function defined
     *  between thw one they wanted to make immediate)
     */

    fun w_immediate(vm: ForthVM) {
        vm.dict.last.imm = true
        if (vm.dict.currentlyDefining == null) {
            vm.dbg(
                0, """Marked '${vm.dict.last}' as immediate-mode
(Put "immediate" inside definition to avoid warning)"""
            )
        }
    }

    /** `RECURSE` IM CO ( -- ) Append call to self to current def */

    fun w_recurse(vm: ForthVM) {
        vm.appendWord(vm.dict.last.name)
    }

    /**
     * `POSTPONE` IM CO ( "<spaces>name" -- ) Add def of name to current def.
     *
     * Postpone is useful for postponing-evaluating a word:
     *
     * For example:
     *   : a 'a' ;
     *   : x immediate postpone a postpone a ;
     *   : y x x ;
     *
     *  'y' is compiled to "a / a / a / a / ret" (not: "x / x / ret").
     *
     *  It can also be used to alias a word:
     *    : my-if immediate postpone if ;
     *    : test 10 my-if 20 then ;
     *
     * Which compiles to the same thing as if "test" used "if" directly.
     */

    fun w_postpone(vm: ForthVM) {
        val token: String = vm.source.scanner.parseName().strFromAddrLen(vm)
        vm.appendWord("[compile]")
        vm.appendWord(token)
    }

    /** `LITERAL` IM CO ( x -- ) Append "lit" and literal x to current def. */

    fun w_literal(vm: ForthVM) {
        vm.appendLit(vm.dstk.pop())
    }
}
