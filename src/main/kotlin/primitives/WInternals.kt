package kf

class WInternals(val vm: ForthVM) : WordClass {
    override val name = "Internals"
    override val primitives: Array<Word> = arrayOf(
        Word(".ip@") { _ -> w_ipLoad() },
        Word(".ip!") { _ -> w_ipStore() },
    )

    fun w_ipLoad() {
        vm.dstk.push(vm.ip)
    }

    fun w_ipStore() {
        vm.ip = vm.dstk.pop()
    }
}