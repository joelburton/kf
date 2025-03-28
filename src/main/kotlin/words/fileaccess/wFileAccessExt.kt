package kf.words.fileaccess

import kf.FileInputSource
import kf.ForthVM
import kf.IWordModule
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
        vm.includedFiles.add(path)
        vm.source.push(FileInputSource(vm, vm.sources.lastIndex + 1, path))
    }

    /**  `include` `( in:"file" -- : read Forth file in )` */

    fun w_include(vm: ForthVM) {
        val path =  vm.source.scanner.parseName().strFromAddrLen(vm)
        include(vm, path)
    }
}