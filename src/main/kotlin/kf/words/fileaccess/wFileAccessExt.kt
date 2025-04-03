package kf.words.fileaccess

import kf.ForthVM
import kf.dict.Word
import kf.dict.w_notImpl
import kf.interfaces.IForthVM
import kf.interfaces.IWordModule
import kf.sources.SourceFile
import kf.strFromAddrLen

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

    fun include(vm: IForthVM, path: String) {
        (vm as ForthVM).includedFiles.add(path)
        vm.source.push(SourceFile(vm, vm.sources.lastIndex + 1, path))
    }

    /**  `include` `( in:"file" -- : read Forth file in )` */

    fun w_include(vm: IForthVM) {
        val path =  vm.source.scanner.parseName().strFromAddrLen(vm)
        include(vm, path)
    }
}