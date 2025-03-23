package kf.words.fileaccess

import com.github.ajalt.mordant.terminal.Terminal
import kf.IntEOF
import kf.IntQuitNonInteractive
import kf.ForthVM
import kf.IWordModule
import kf.ForthInterrupt
import kf.TerminalFileInterface
import kf.Word
import kf.strFromAddrLen
import kf.w_notImpl

object wFileAccessExt: IWordModule {
    override val name = "kf.words.fileaccess.wFileAccessExt"
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

    fun include(vm: ForthVM, path: String) {
        val prevIO: Terminal = vm.io
        val prevVerbosity: Int = vm.verbosity
        val prevSourceId: Int = vm.sourceId

        vm.io = Terminal(terminalInterface = TerminalFileInterface(path))
        vm.verbosity = -2
        vm.includedFiles.add(path)
        vm.sourceId = vm.includedFiles.lastIndex + 1

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
            vm.sourceId = prevSourceId
            vm.verbosity = prevVerbosity
        }
    }

    /**  `include` `( in:"file" -- : read Forth file in )` */

    fun w_include(vm: ForthVM) {
        val path =  vm.scanner.parseName().strFromAddrLen(vm)
        include(vm, path)
    }
}