package kf.primitives

import com.github.ajalt.mordant.terminal.muted
import kf.ForthVM
import kf.Word
import kf.WordClass

object WCompiling : WordClass {
    override val name = "Compiling"

    override val primitives: Array<Word> = arrayOf(
        Word(":", ::w_colon, interpO = true),
        Word(";", ::w_semicolon ,imm = true, compO = true),
        Word("[literal]", ::w_bracketLiteral, compO = true),
        Word("literal", ::w_literal, imm = true, compO = true),
        Word("[']", ::w_bracketTick, imm = true, compO = true),
        Word("immediate", ::w_immediate, imm = true),
        Word("recursive", ::w_recursive, imm = true, compO = true ),
        Word("postpone", ::w_postpone, imm = true, compO = true),
        Word("dolit", ::w_doLit),
    )

    /**  `:` X ( in:"name" -- : create word 'name' and start compiling mode )
     */
    fun w_colon(vm: ForthVM) {
        val name: String = vm.getToken()

        // Words start off not-recursive hidden, so they can't call themselves
        // while still being compiled. This allows:
        //
        //   : a 65 emit ;
        //   : a a ;         <- new `a` is calling previous a
        //
        // Making the function recursive (`recursive`) will mark the
        // currently-being-defined word as such while being compiled,
        // so it can call itself (recurse).

        val wCall = vm.dict["call"]
        val w = Word(
            name,
            cpos = vm.cend,
            dpos = Word.Companion.NO_ADDR,
            fn = wCall.fn
        )
        vm.dict.add(w)
        vm.interpState = ForthVM.Companion.INTERP_STATE_COMPILING
        vm.dict.currentlyDefining = w
    }

    /** `;` IC ( -- : complete word definition and exit compiling mode )`
     */
    fun w_semicolon(vm: ForthVM) {
        val w: Word = vm.dict.last
        vm.appendWord("return")
        w.cposEnd = vm.cend
        vm.dict.currentlyDefining = null
        vm.interpState = ForthVM.Companion.INTERP_STATE_INTERPRETING
    }

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

    /** `dolit` ( -- 'lit : push wn for 'lit' onto stack )
     */
    fun w_doLit(vm: ForthVM) {
        val wn: Int = vm.dict.getNum("lit")
        vm.dstk.push(wn)
    }

    /**  `[']` IC ( s:"name" -- : puts lit/wn in code )
     *
     * - imm mode: warns & pushes wn to dstk
     * - normal comp fn: writes lit/wn-of-dup
     * can use in fn, like `: b a ;`
     * - imm-mode fn: call/immediate/then-same (I think this is same thing])
     * but can't put in fn, like `: b a ;`
     * perhaps imm-mode-in-immediate-already is bad?
     */
    fun w_bracketTick(vm: ForthVM) {
        val token: String = vm.getToken()
        val wn: Int = vm.dict.getNum(token)
        vm.appendLit(wn)
    }

    /** `immediate` I ( -- : mark current-or-most-recent word "immediate" )
     *
     * Warns about using immediate like this:
     * : a 65 emit ; immediate
     * Doesn't warn for usage like this:
     * : a 65 emit immediate ;
     *
     * (since it can be a little bug-prone to mark-most-recent-as-immediate
     * when the user may have forgotten there was another function defined
     * between thw one they wanted to make immediate)
     */
    fun w_immediate(vm: ForthVM) {
        val w: Word = vm.dict.last
        w.imm = true
        if (vm.dict.currentlyDefining == null) {
            vm.dbg(
                0, """Marked '$w' as immediate-mode
(Put "immediate" inside definition to avoid warning)"""
            )
        }
    }

    /** `recursive` CI ( -- : from this point onward, word can recurse )
     */
    fun w_recursive(vm: ForthVM) {
        val w: Word = vm.dict.currentlyDefining!!
        w.recursive = true
    }


    /**  postpone
     */
    fun w_postpone(vm: ForthVM) {
        val token: String = vm.getToken()
        val w: Word = vm.dict[token]
        if (w.imm) {
            vm.dict.last.fn = w.fn
        } else {
            vm.io.muted("??? using `postpone` with non-immediate word")
        }
    }

    /**  `bracketLiteral` C ( n -- : write lit token & n to code area )
     */
    fun w_bracketLiteral(vm: ForthVM) {
        val v: Int = vm.dstk.pop()
        vm.appendLit(v)
    }

    /**  `literal` IC ( n -- : write lit token & n to code area )
     */
    fun w_literal(vm: ForthVM) {
        val v: Int = vm.dstk.pop()
        vm.appendLit(v)
    }
}