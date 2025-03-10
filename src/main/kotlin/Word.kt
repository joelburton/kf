package kf


class Word(
    val name:String,
    var cpos: Int = NO_ADDR,
    var dpos: Int = NO_ADDR,
    var hidden: Boolean = false,
    var immediate: Boolean = false,
    var compileOnly: Boolean = false,
    var interpOnly: Boolean = false,
    var recursive: Boolean = false,
    var wn: Int? = null,
    val callable: (ForthVM) -> Unit,
) {
   companion object {
       val NO_ADDR: Int = 0xffff
       val noWord = Word("noWord", hidden=true, callable = { _: ForthVM -> })
   }

    override fun toString() = name;
    fun exec(vm: ForthVM) = callable(vm)

    /**  Useful for debugging and to support `w_see` and `w_simple-see`. */
    fun getHeaderStr(io: IOBase): String {
        return java.lang.String.format(
            "%s %-32s %-2s %-2s %-2s %-2s %-2s C:%-5s D:%-5s\n",
            io.grey(String.format("(%3d)", wn)),
            io.yellow(name),
            if (immediate) "IM" else "",
            if (compileOnly) "CO" else "",
            if (interpOnly) "IO" else "",
            if (recursive) "RE" else "",
            if (hidden) "HI" else "",
            if (cpos != NO_ADDR) String.format("$%04x", cpos) else "",
            if (dpos != NO_ADDR) String.format("$%04x", cpos) else ""
        )
    }
}