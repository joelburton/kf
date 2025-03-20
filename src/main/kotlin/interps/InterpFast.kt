package kf.interps

import kf.ForthVM
import kf.Word

class InterpFast(vm: ForthVM) : InterpEval(vm) {
    override val name = "Fast"
    override val code = """
        begin 
            refill while
                begin
                    parse-name dup while
                        interp-process-token
                    repeat
                    2drop
                interp-prompt
            repeat
        eof
        """

    override fun reboot() {
        vm.dict.add(Word("INTERP-PROCESS-TOKEN", ::w_processToken))
        super.reboot()
    }

}