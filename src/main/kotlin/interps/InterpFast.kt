package kf.interps

import kf.ForthVM
import kf.Word

class InterpFast(vm: ForthVM) : InterpCLI(vm) {
    override val name = "Fast"
    override val code = """
        ] begin 
            refill while
                process-line
                interp-prompt
            repeat
        eof [
        """

    override fun rebootInterpreter() {
        vm.dict.add(Word("process-line", ::w_processLine))
        super.rebootInterpreter()
    }
}