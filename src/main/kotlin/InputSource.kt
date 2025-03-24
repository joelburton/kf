package kf

import com.github.ajalt.mordant.platform.MultiplatformSystem


abstract class InputSource(val vm: ForthVM, val id: Int, val path: String) {
    var ptr: Int = 0
    var lineCount: Int = 0
    var storedInPtr: Int = 0
    abstract fun readLineOrNull(): String?
    override fun toString() = "$id:$path:$lineCount"

    /**  Scanner for reading and tokenizing input line. */

    var scanner: FScanner = FScanner(vm)

    /** Push a new input source on top of this one, keeping track of inptr */
    fun push(newSrc: InputSource) {
        if (vm.sources.isNotEmpty()) vm.source.storedInPtr = vm.inPtr
        vm.sources.add(newSrc)
        vm.inPtr = 0
    }

    /** Pop this input source off stack, or throw EOF if no more */
    fun pop() {
        vm.sources.removeLast()
        if (vm.sources.isEmpty()) throw IntEOF()
        vm.inPtr = vm.source.storedInPtr
        vm.ip = vm.cstart
    }
}

open class FFileSource(
    vm: ForthVM, id: Int, path: String) : InputSource(vm, id, path) {
    open val content: String =
        MultiplatformSystem.readFileAsUtf8(path).toString()

    override fun readLineOrNull(): String? {
        if (ptr >= content.length) return null
        var s = String()
        while (ptr < content.length && content[ptr++] != '\n') {
            s += content[ptr - 1]
        }
        lineCount += 1
        return s
    }
}

class StdInInputSource(vm: ForthVM) : InputSource(vm, 0, "<stdin>") {
    override fun readLineOrNull(): String? {
        lineCount += 1
        return readlnOrNull()
    }
}

class EvalInputSource(
    vm: ForthVM, override val content: String) : FFileSource(vm, -1, "<eval>")

