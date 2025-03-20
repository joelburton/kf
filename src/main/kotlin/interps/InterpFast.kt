package kf.interps

import kf.ForthVM
import kf.Word

class InterpFast(vm: ForthVM) : InterpCLI(vm) {
    override val name = "Fast"
    override val code = """
        begin 
            refill while
                begin
                    parse-name dup while
                        interp-process-token
                    repeat
                    drop drop
                interp-prompt
            repeat
        eof
        """

    override fun rebootInterpreter() {
//        vm.dict.add(Word("INTERP-PROCESS-LINE", ::w_processLine))
        vm.dict.add(Word("INTERP-PROCESS-TOKEN", ::w_processToken))
        super.rebootInterpreter()
    }
}