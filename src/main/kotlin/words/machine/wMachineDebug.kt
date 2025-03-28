package kf.words.machine

import kf.ForthVM
import kf.IWordModule
import kf.Word

object wMachineDebug : IWordModule {
    override val name = "kf.words.machine.wMachineDebug"
    override val description = "Custom internal words needed for VM"

    override val words = arrayOf<Word>(
        Word("(FOO)", ::w_parenFoo),
        Word("(.)", ::w_parenDot),
        Word("(WORDS)", ::w_parenWords)
    )

    fun w_parenWords(vm: ForthVM) {
        for (word in vm.dict.words) {
            print("${word.name} ")
        }
    }
    fun w_parenFoo(vm: ForthVM) {
        vm.io.print("foo")
    }

    fun w_parenDot(vm: ForthVM) {
        vm.io.print("${vm.dstk.pop()} ")
    }
}
