package kf.words

import kf.D
import kf.ForthVM
import kf.ForthVM.Companion.INTERP_STATE_INTERPRETING
import kf.primitives.WInterp.w_banner

open class InterpBase(val vm: ForthVM) {

    /**  Handle a VM reboot at the interpreter layer.
     */
    fun rebootInterpreter() {
        if (D) vm.dbg(3, "vm.rebootInterpreter")
        vm.interpScanner.reset()

        // Put interpreter code in mem; the VM will start executing here
        addInterpreterCode()
        resetInterpreter()
        w_banner(vm)
    }

    /**  Handle a VM reset at the interpreter layer.
     */
    fun resetInterpreter() {
        if (D) vm.dbg(3, "vm.resetInterpreter")

        // If error happens while defining word, roll back this word.
        vm.dict.currentlyDefining?.let { w ->
            vm.cend = w.cpos
            vm.dict.removeLast()
            vm.dict.currentlyDefining = null
        }
        vm.interpState = INTERP_STATE_INTERPRETING
    }

    open fun addInterpreterCode() {}
}