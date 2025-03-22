package kf.words.fileaccess

import com.github.ajalt.mordant.terminal.Terminal
import kf.IntEOF
import kf.IntQuitNonInteractive
import kf.ForthVM
import kf.IWordClass
import kf.ForthInterrupt
import kf.TerminalFileInterface
import kf.Word
import kf.strFromAddrLen
import kf.w_notImpl

object wFileAccessExt: IWordClass {
    override val name = "FileAccessExt"
    override val description = "File access"
    override val words = arrayOf<Word>(
        Word("FILE-STATUS", ::w_notImpl),
        Word("FLUSH-FILE", ::w_notImpl),
        Word("INCLUDE", ::w_include),
        Word("RENAME-FILE", ::w_notImpl),
        Word("REQUIRE", ::w_notImpl),
        Word("REQUIRED", ::w_notImpl),
    )


    // *************************************************************************

    /**  `include` `( in:"file" -- : read Forth file in )` */

    fun w_include(vm: ForthVM) {
        val path =  vm.scanner.parseName().strFromAddrLen(vm)

        val prevIO: Terminal = vm.io
        val prevVerbosity: Int = vm.verbosity

        vm.io = Terminal(terminalInterface = TerminalFileInterface(path))
        vm.verbosity = -2
        try {
            vm.runVM()
        } catch (e: ForthInterrupt) {
            when (e) {
                is IntQuitNonInteractive -> vm.ip = vm.memConfig.codeStart
                is IntEOF -> vm.ip = vm.memConfig.codeStart
                else -> throw e  // let outer interpreter handle
            }
        } finally {
            vm.io = prevIO
            vm.verbosity = prevVerbosity
        }
    }

}