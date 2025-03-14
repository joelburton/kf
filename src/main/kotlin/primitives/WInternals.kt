package kf.primitives

import kf.ForthVM
import kf.Word
import kf.WordClass

object WInternals : WordClass {
    override val name = "Internals"
    override val primitives: Array<Word> = arrayOf(
        Word(".ip@", ::w_ipLoad),
        Word(".ip!", ::w_ipStore),
    )

    fun w_ipLoad(vm: ForthVM) {
        vm.dstk.push(vm.ip)
    }

    fun w_ipStore(vm: ForthVM) {
        vm.ip = vm.dstk.pop()
    }
}