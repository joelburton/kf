package kf.words.custom

import kf.ForthVM
import kf.interfaces.IWordModule
import kf.dict.Word
import kf.interfaces.IForthVM

object wIOCustom : IWordModule {
    override val name = "kf.words.custom.wIOCustom"
    override val description = "Custom words for IO"
    override val words = arrayOf<Word>(
        Word("NL", ::w_nl),
        Word(".INCLUDED-FILES", ::w_dotIncludedFiles),
        Word(".INPUT-SOURCES", ::w_dotInputSources),
    )

    /** `nl` ( -- nlChar : return newline char )` */

    fun w_nl(vm: IForthVM) {
        vm.dstk.push(0x0a)
    }

    /** `.INCLUDED-FILES` ( -- ) Print included files list */

    fun w_dotIncludedFiles(vm: IForthVM) {
        print((vm as ForthVM).includedFiles.joinToString("\n"))
    }

    fun w_dotInputSources(vm: IForthVM) {
        println("Input sources:")
        println(vm.sources.joinToString("\n") { "${it.id}: ${it.path}" })
    }


}