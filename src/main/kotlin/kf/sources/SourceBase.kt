package kf.sources

import kf.interps.FScanner
import kf.ForthVM
import kf.IntEOF
import kf.interfaces.ISource

/** ABC of all input sources. */

abstract class SourceBase(
    val vm: ForthVM,
    override val id: Int,
    override val path: String
) : ISource {
    override var ptr: Int = 0

    override var lineCount: Int = 0

    /** Copy stashed away of the scanner's pointer to where it's tokenized.
     *
     * In theory, this could just be a property of the scanner instance.
     * However, ANS Forth requires us to have a word `>IN` that returns an
     * address of a cell with this info, and it can be changed for users to
     * change where the scanner is. Therefore, it needs to be stored in a
     * spot in RAM. As such, we need to tuck away where this way when a child
     * source is about to pushed over us (since it will have its own >IN,
     * overwriting ours. When that child is popped, this can be restored.
     * */
    var storedInPtr: Int = 0

    abstract override fun readLineOrNull(): String?

    /** Used in error messages, like '<0:stdin>:42` or `4:<foo.fth>:17` */
    override fun toString() = "$id:$path:$lineCount"

    override val scanner: FScanner = FScanner(vm)

    override fun push(newSrc: ISource) {
        if (vm.sources.isNotEmpty()) (vm.source as SourceBase).storedInPtr =
            vm.inPtr
        vm.sources.add(newSrc)
        vm.inPtr = 0
    }

    override fun pop() {
        vm.sources.removeLast()
        if (vm.sources.isEmpty()) throw IntEOF()
        vm.inPtr = (vm.source as SourceBase).storedInPtr
        vm.ip = vm.cstart
    }
}
