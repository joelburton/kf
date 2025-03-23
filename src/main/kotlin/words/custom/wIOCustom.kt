package kf.words.custom

import kf.ForthVM
import kf.IWordModule
import kf.Word

object wIOCustom : IWordModule {
    override val name = "kf.words.custom.wIOCustom"
    override val description = "Custom words for IO"
    override val words = arrayOf<Word>(
        Word("NL", ::w_nl),
        Word(".INCLUDED-FILES", ::w_dotIncludedFiles),
    )

    /** `nl` ( -- nlChar : return newline char )` */

    fun w_nl(vm: ForthVM) {
        vm.dstk.push(0x0a)
    }

    /** `.INCLUDED-FILES` ( -- ) Print included files list */

    fun w_dotIncludedFiles(vm: ForthVM) {
        print(
            vm.includedFiles.indices.joinToString("\n")
            { idx -> "$idx: ${vm.includedFiles[idx]}" })
    }


}