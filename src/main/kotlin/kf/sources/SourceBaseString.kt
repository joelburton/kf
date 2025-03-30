package kf.sources

import kf.ForthVM

/** Abstract layer for read-from-string input sources. */

open class SourceBaseString(
    vm: ForthVM, id: Int, path: String
) : SourceBase(vm, id, path) {
    // slurp, slurp, slurp, that's tasty unicode
    open val content: String  = ""

    /** Read line and return null at end of file. */

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
