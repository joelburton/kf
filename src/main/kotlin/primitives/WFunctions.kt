package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass

object WFunctions : WordClass {
    override val name = "Functions"

    override val primitives get() = arrayOf(
//        Word("call", ::w_call),
        Word("call-by-addr", ::w_callByAddr),

        // This is the same def as exit -- but this is what's added at the
        // end a colon def, whereas exit is what a user might use in a function.
        // This allows the system to better decompile a word, knowing it ends
        // at the ;s, not at some random exit
//        Word(";s", ::w_exit ),
        )

    /**  `call-by-addr` ( addr -- : call addr )
     */
    fun w_callByAddr(vm: ForthVM) {
        val addr: Int = vm.dstk.pop()
        vm.rstk.push(vm.ip)
        vm.ip = addr
    }


}