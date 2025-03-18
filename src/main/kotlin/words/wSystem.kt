package kf.words

import kf.D
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.w_notImpl


object wSystem: IWordClass {
    override val name = "System"
    override val description = "The system outside of the VM"

    override val words
        get() = arrayOf(
            Word("COLD", ::w_reboot, imm = true, interpO = true),
            Word("COLD-RAW", ::w_rebootRaw, imm = true, interpO = true),
            Word("ENVIRONMENT?", ::w_notImpl)
        )

    /** `reboot` ( -- : Reboots machine {clear all mem, stacks, state, etc.} )
     */
    fun w_reboot(vm: ForthVM) {
        vm.reboot(true)
    }

    /** `reboot-raw` ( -- : Reboots machine and load minimal primitives )
     *
     * The only primitives loaded will be these and the ones required for the
     * interpreter itself. The rest would need to be loaded with
     * `include-primitives` (which, fortunately, is provided by the
     * interpreter :-)
     */
    fun w_rebootRaw(vm: ForthVM) {
        vm.reboot(false)
    }


}