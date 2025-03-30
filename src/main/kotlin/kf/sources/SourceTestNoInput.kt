package kf.sources

import kf.ForthVM

/** Input source for non-functional tests; that doesn't use any input. */
class SourceTestNoInput(vm: ForthVM) : SourceBaseString(vm, 0, "<fake>") {
    override val content get() = throw Exception("No input available")
}
