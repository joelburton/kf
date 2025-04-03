package kf.words.machine

import kf.dict.Word
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule

object wMachineDebug : IWordModule {
    override val name = "kf.words.machine.wMachineDebug"
    override val description = "Custom internal words needed for VM"

    override val words = arrayOf<Word>(
        Word("(FOO)", ::w_parenFoo),
        Word("(.)", ::w_parenDot),
        Word("(WORDS)", ::w_parenWords)
    )

    fun w_parenWords(vm: IForthVM) {
        for (word in vm.dict.words) {
            print("${word.name} ")
        }
    }
    fun w_parenFoo(vm: IForthVM) {
        vm.io.print("foo")
    }

    fun w_parenDot(vm: IForthVM) {
        vm.io.print("${vm.dstk.pop()} ")
    }
}
