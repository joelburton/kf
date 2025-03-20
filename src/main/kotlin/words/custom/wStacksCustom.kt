package kf.words.custom

import kf.ForthError
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.words.machine.wMachine.w_nop

object wStacksCustom: IWordClass {
    override val name = "Stack Operations"
    override val description = "Stack custom funcs"
    override val words get() = arrayOf(

        Word("CLEARSTACK", ::w_clearStack ) ,

        Word("SP0", ::w_sp0 ) ,
        Word("SP@", ::w_spFetch ) ,
        Word("SP!", ::w_spStore ) ,

        Word("RP0", ::w_rp0 ) ,
        Word("RP@", ::w_rpFetch ) ,
        Word("RP!", ::w_rpStore ) ,

        Word("?STACK", ::w_nop)
        // clearstacks : clears data & fp, not other things!

    )

    /**  `sp0` ( -- addr : address of start of stack )
     */
    private fun w_sp0(vm: ForthVM) {
        vm.dstk.push(vm.dstk.startAt - 1)
    }

    /**  `sp0` ( -- addr : address of start of stack )
     */
    private fun w_rp0(vm: ForthVM) {
        vm.dstk.push(vm.rstk.startAt - 1)
    }

    /**  `clearstack` ( ? ? -- : clear entire data stack )
     */
    private fun w_clearStack(vm: ForthVM) {
        vm.dstk.reset()
    }


    /**  `sp@` ( -- addr : pushes dstk sp to stack )
     */
    fun w_spFetch(vm: ForthVM) {
        vm.dstk.push(vm.dstk.sp)
    }

    /**  `sp!` ( addr -- : sets dstk sp to addr )
     */
    fun w_spStore(vm: ForthVM) {
        val sp: Int = vm.dstk.pop()
        if (sp < -1 || sp >= vm.dstk.endAt) {
            throw ForthError("Invalid sp")
        }
        vm.dstk.sp = sp
    }

    /**  `rp@` ( -- addr : pushes rstk sp to stack )
     */
    fun w_rpFetch(vm: ForthVM) {
        vm.dstk.push(vm.rstk.sp)
    }

    /**  `rp!` ( addr -- : sets rstk sp to addr )
     */
    fun w_rpStore(vm: ForthVM) {
        val sp: Int = vm.dstk.pop()
        if (sp < -1 || sp >= vm.rstk.endAt) {
            throw ForthError("Invalid sp")
        }
        vm.rstk.sp = sp
    }


}