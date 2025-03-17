package kf

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.warning

typealias StaticFunc = (ForthVM) -> Unit

class Word(
    val name: String,
    var fn: StaticFunc,
    var cpos: Int = NO_ADDR,
    var dpos: Int = NO_ADDR,
    var hidden: Boolean = false,
    var imm: Boolean = false,
    var compO: Boolean = false,
    var interpO: Boolean = false,
    var recursive: Boolean = false,
    var deferToWn: Int? = null,
    var wn: Int = 0,
) {
    companion object {
        fun noWordFn(vm: ForthVM) {
            vm.io.warning("No Word Fn ran")
        }
        const val NO_ADDR: Int = 0xffff
        val noWord = Word("noWord", ::noWordFn, hidden = true)

        /**  Explanation for header strings */
        const val HEADER_STR: String =
            " IMmediate Compile-Only Interp-Only REcurse HIdden Code Data"


    }

    override fun toString() = name
    operator fun invoke(vm: ForthVM) {
        vm.currentWord = this
        if (D) {
            var s = gray(fn.toString()
                .removeSuffix("(kf.ForthVM): kotlin.Unit")
                .removePrefix("fun "))
            vm.dbg(2, "x@ ${(vm.ip-1).addr} -> $name $s")
            vm.dbg_indent += 1
        }
        fn(vm)
        if (D) {
            vm.dbg_indent -= 1
            vm.dbg(3, "x@ ${vm.ip.addr} <- $name")
        }
    }

    /**  Useful for debugging and to support `w_see` and `w_simple-see`. */
    fun getHeaderStr(): String {
        return java.lang.String.format(
            "%s %-32s %-2s %-2s %-2s %-2s %-2s C:%-5s D:%-5s\n",
            gray(String.format("(%3d)", wn)),
            yellow(name),
            if (imm) "IM" else "",
            if (compO) "CO" else "",
            if (interpO) "IO" else "",
            if (recursive) "RE" else "",
            if (hidden) "HI" else "",
            if (cpos != NO_ADDR) cpos.addr else "",
            if (dpos != NO_ADDR) dpos.addr else ""
        )
    }

    fun isSameExec(other: Word): Boolean =
        this.fn == other.fn
                && this.cpos == other.cpos
                && this.dpos == other.dpos
}