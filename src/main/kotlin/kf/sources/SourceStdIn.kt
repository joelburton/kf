package kf.sources

import kf.ForthVM

/** Interactive stdin source. */

class SourceStdIn(vm: ForthVM) : SourceBase(vm, 0, "<stdin>") {

    /** Get a line from a real console or something piped in via the shell.
     *
     * Returns NULL at end of pipe or when console user uses Control-D
     * (or Control-Z for any Forth-loving Windows users)
     * */

    override fun readLineOrNull(): String? {
        lineCount += 1
        val result = vm.io.readLine()
        // Space between input and any output
        vm.io.print(" ")
        return result
    }
}
