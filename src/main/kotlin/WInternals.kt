package kf

class WInternals(val vm: ForthVM) {
    val primitives: Array<Word> = arrayOf<Word>(
        Word(".cptr") { _ -> w_cptrLoad() },
        Word(".cptr!") { _ -> w_cptrStore() },
    )

    fun w_cptrLoad() {
        vm.dstk.push(vm.cptr)
    }

    fun w_cptrStore() {
        vm.cptr = vm.dstk.pop()
    }
}