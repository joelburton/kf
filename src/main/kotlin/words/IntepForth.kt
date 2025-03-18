package kf.words

import kf.CellMeta
import kf.D
import kf.ForthVM
import kf.InvalidState
import kf.Word
import kf.isCharLit
import kf.toForthInt

class InterpForth(vm: ForthVM) : InterpBase(vm) {
    fun _compile(token: String) {
        if (D) vm.dbg(3, "vm.interpCompile: $token")
        val w: Word? = vm.dict.getSafeChkRecursion(token, vm.io)

        if (w != null) {
            if (w.interpO)
                throw InvalidState("Can't use in compile mode: ${w.name}")
            else if (w.imm) {
                w(vm)
            } else {
                vm.appendCode(w.wn, CellMeta.WordNum)
            }
        } else if (token.isCharLit) {
            vm.appendLit(token[1].code)
        } else {
            val n: Int = token.toForthInt(vm.base)
            vm.appendWord("lit")
            vm.appendCode(n, CellMeta.NumLit)
        }
    }


    /** Instructions for the VM for the Forth interpreter
     *
     * This is poked into memory during the reboot process; this is the
     * interpreter loop:
     *
     * - show prompt
     *
     * - read line of input (from terminal/file/wherever io tells us)
     * - if null, jump to EOF-point, below
     *
     * - read next token from input
     * - if null, jump back to show-prompt
     *
     * - call w_processToken
     *
     * - go back to read-next-token
     *
     * - EOF: we get here when no more input from io system
     * - execute w_eof, which throws an EOF error
     *
     * That's normally going to stop the interpreter. However, if the
     * interpreter is getting code from files, it might just move onto
     * the next file. Or, if a terminal-user uses "include ..." to read
     * from a file, after an EOF in that file, it will cede control back
     * to the console.
     *
     * However, in the general case, this ends the
     * session with the VM and the program ultimately stops.
     *
     * - In cases where the IO subsystem gets more input (another file
     * or returning from file-reading to the console user), the EOF
     * won't be fatal, so jump back the show-prompt top and continue.
     *
     * The w_processToken word right now is just a switch between calling
     * the Java code for interpExecute and interpCompile, but maybe one day
     * more of this will be done at the Forth level, allowing users to
     * customize their own interpreters more without less reliance on part of
     * that loop being locked up in non-word code: that would require exposing
     * more of the actual dictionary access to Forth for people to be able
     * to write more interpreter internals in Forth. */

    override fun addInterpreterCode() {
        if (D) vm.dbg(3, "vm.addInterpreterCode")

        // fixme: can't use lots of things here because this method is too
        //   fake --- the split-on-whitespace doesn't act like the real
        //   interpreter for parsing

        """
        begin 
            refill while
                begin
                    word dup @ while
                        state @ if
                            find ?dup if
                                1 = if
                                    execute
                                else
                                    ,,
                                then
                            else
                                count 0 0 2swap >number drop drop drop
                                do-lit ,, ,,
                            then
                        else
                            find if
                                execute
                            else
                                count 0 0 2swap >number drop drop drop
                            then
                        then
                    repeat
                drop
                3 spaces        
                111 emit 107 emit
                cr
            repeat
        eof
        """
            .trimIndent()
            .split(Regex("\\s+"))
            .forEach { _compile(it) }
            .also { wToolsExtra.w_dumpCode(vm) }
    }
}