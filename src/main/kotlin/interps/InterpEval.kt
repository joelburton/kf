package kf.interps

import kf.CellMeta
import kf.D
import kf.ForthVM
import kf.InvalidState
import kf.Word
import kf.strFromAddrLen
import kf.isCharLit
import kf.toForthInt
import kf.words.custom.wToolsCustom

/** Base class for interpreters that don't have a CLI, but can evaluate
 * and compile. */

open class InterpEval(vm: ForthVM) : InterpBase(vm) {
    override val name = "Eval"
    override val code = """
        branch [ 4 ,, ]    
        [ : foofoo (foo) (foo) ; ] 
        foofoo
    """

    override fun rebootInterpreter() {
        if (D) vm.dbg(3, "InterpEval.rebootInterpreter")

        super.rebootInterpreter()
        scanner.reset()
    }

    override fun resetInterpreter() {
        if (D) vm.dbg(3, "InterpEval.resetInterpreter")

        // If error happens while defining word, roll back this word.
        vm.dict.currentlyDefining?.let { w ->
            vm.cend = w.cpos
            vm.dict.removeLast()
            vm.dict.currentlyDefining = null
        }
        state = STATE_INTERPRETING
    }

    /** Called by w_processToken when InterpretedMode is "compiling":
     *
     * Most parts in the definition and just added directly.
     * However, words that are "immediate-mode" will execute.
     */

    override fun _compile(token: String) {
        if (D) vm.dbg(3, "vm.interpCompile: $token")
        val w: Word? = vm.dict.getSafeChkRecursion(token, vm.io)

        if (w != null) {
            if (w.interpO)
                throw InvalidState("Can't use in compile mode: ${w.name}")
            else if (w.imm) {
                w(vm)
            } else {
                vm.appendCode(w.wn, CellMeta.WordNum)
            }
        } else if (token.isCharLit) {
            vm.appendLit(token[1].code)
        } else {
            val n: Int = token.toForthInt(vm.base)
            vm.appendWord("lit")
            vm.appendCode(n, CellMeta.NumLit)
        }
    }

    override fun _interpret(token: String) {
        if (D) vm.dbg(3, "vm.interpInterpret: $token")
        val w: Word? = vm.dict.getSafe(token)
        if (w != null) {
            if (w.compO) throw InvalidState("Compile-only: " + w.name)
            w(vm)
        } else if (token.isCharLit) {
            vm.dstk.push(token[1].code)
        } else {
            vm.dstk.push(token.toForthInt(vm.base))
        }
    }

    override fun eval(line: String) {
        // fixme: flawed, but needed for bootstrapping interp code
        scanner.fill(line)
        w_processLine(vm)
    }

    fun w_processToken(vm: ForthVM) {
        val len = vm.dstk.pop()
        val addr = vm.dstk.pop()
        var token = Pair(addr, len).strFromAddrLen(vm)
        if (D) vm.dbg(3, "vm.w_processToken")
        if (isInterpreting) _interpret(token)
        else _compile(token)
    }

    fun w_processLine(vm: ForthVM) {
        // fixme: flawed, but curr needed for eval
        while (true) {
            val name = getToken()
//            val name = scanner.parseNameToStr()
            if (name.length == 0) break
            if (isInterpreting) _interpret(name)
            else _compile(name)
        }
    }

    override fun addInterpreterCode() {
        if (D) vm.dbg(3, "vm.addInterpreterCode")
        state = STATE_COMPILING
        code.split("\n")
            .forEach { eval(it) }
            .also { if (D && vm.verbosity > 1) wToolsCustom.w_dotCode(vm) }
    }


}