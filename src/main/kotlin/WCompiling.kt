package kf

class WCompiling(val  vm: ForthVM) {
    val primitives: Array<Word> = arrayOf<Word>(
        Word(":", interpOnly = true) { _ -> w_colon() },
        Word(";", immediate=true, compileOnly=true) { _ -> w_semicolon() },
        Word("literal", immediate=true, compileOnly=true) { _ -> w_bracketLiteral() },
        Word("[literal]", compileOnly=true) { _ -> w_literal() },
        Word("[']", immediate=true, compileOnly=true) { _ -> w_bracketTick() },
        Word("immediate", immediate = true) { _ -> w_immediate() },
        Word("recursive", immediate=true, compileOnly=true) { _ -> w_recursive() },
        Word("postpone", immediate=true, compileOnly=true) { _ -> w_postpone() },
        Word("dolit") { _ -> w_doLit() },
    )

    /**  `:` X ( in:"name" -- : create word 'name' and start compiling mode ) */
    fun w_colon() {
        if (D) vm.dbg("w_colon")

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

        val wCall = vm.dict.get("call")
        val w = Word(name, cpos=vm.cend, dpos=Word.NO_ADDR, callable = wCall.callable)
        vm.dict.add(w)
        vm.interpState = vm.INTERP_STATE_COMPILING
        vm.dict.currentlyDefining = w
    }

    /** `;` IC ( -- : complete word definition and exit compiling mode )` */
    fun w_semicolon() {
        val w: Word = vm.dict.last
        if (D) vm.dbg(2, "w_semicolon: %s", w.name)
        vm.appendWord("return")
        vm.dict.currentlyDefining = null
        vm.interpState = vm.INTERP_STATE_INTERPRETING
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
    fun w_doLit() {
        if (D) vm.dbg("w_doLit")
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
    fun w_bracketTick() {
        val token: String = vm.getToken()
        val wn: Int = vm.dict.getNum(token)
        if (D) vm.dbg("w_bracketTick: '%s' wn=%d", token, wn)
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
    fun w_immediate() {
        vm.dbg("w_immediate")
        val w: Word = vm.dict.last
        w.immediate = true
        if (vm.dict.currentlyDefining == null) {
            vm.dbg(
                0, """Marked '$w' as immediate-mode
(Put "immediate" inside definition to avoid warning)"""
            )
        }
    }

    /** `recursive` CI ( -- : from this point onward, word can recurse )
     */
    fun w_recursive() {
        val w: Word = vm.dict.currentlyDefining!!
        w.recursive = true
        vm.dbg("w_recursive: $w")
    }


    /**  postpone */
    fun w_postpone() {
        val token: String = vm.getToken()
        vm.dbg("w_postpone: '%s'", token)
        val w: Word = vm.dict.get(vm.dict.getNum(token))
        val me: Word = vm.dict.last // currently defining word?
// FIXME: need to fix, since callable can't be changed rn
//        if (w.immediate) {
//            me.callable = w.callable
//        } else {
//            vm.dbg("HUH ? w_postpone: $w")
//        }
    }

    /**  `bracketLiteral` C ( n -- : write lit token & n to code area ) */
    fun w_bracketLiteral() {
        val `val`: Int = vm.dstk.pop()
        vm.dbg("w_doLiteral %d", `val`)
        vm.appendLit(`val`)
    }

    /**  `literal` IC ( n -- : write lit token & n to code area ) */
    fun w_literal() {
        val `val`: Int = vm.dstk.pop()
        vm.dbg("w_literal %d", `val`)
        vm.appendLit(`val`)
    }
}