package kf.sources

import kf.ForthVM

/** A simple input source for EVALUATE: it's given a string that's its input. */

class SourceEval(
    vm: ForthVM, override var content: String
) : SourceBaseString(vm, -1, "<eval>")
