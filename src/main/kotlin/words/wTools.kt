package kf.words

import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.Whitespace
import kf.ForthVM
import kf.IWordClass
import kf.Word
import kf.addr
import kf.hex8
import kf.w_notImpl
import kf.words.wDoubleCell.w_mStar

object wTools: IWordClass {
    override val name = "Tools"
    override val description = "Programming tools"

    override val words
        get() = arrayOf(
            Word(".S", ::w_dotS),
            Word("?", ::w_question),
            Word("DUMP", ::w_dump),
            Word("SEE", ::w_see),
            Word("WORDS", ::w_words),
        )

    /** .S   dot-s   TOOLS
     *
     * ( -- )
     *
     * Copy and display the values currently on the data stack. The format of
     * the display is implementation-dependent.
     *
     * .S may be implemented using pictured numeric output words. Consequently,
     * its use may corrupt the transient region identified by #>.
     */

    fun w_dotS(vm: ForthVM) {
        vm.dstk.simpleDump()
    }

    /** WORDS    TOOLS
     *
     * ( -- )
     *
     * List the definition names in the first word list of the search order.
     * The format of the display is implementation-dependent.
     *
     * WORDS may be implemented using pictured numeric output words.
     * Consequently, its use may corrupt the transient region identified by #>.
     */

    fun w_words(vm: ForthVM) {
        vm.io.println(
            vm.dict.words.filter { !it.hidden }.joinToString(" ") { it.name },
            whitespace = Whitespace.NORMAL,
            overflowWrap = OverflowWrap.BREAK_WORD
        )
    }

    /** ?    question    TOOLS
     *
     * ( a-addr -- )
     *
     * Display the value stored at a-addr.
     *
     * ? may be implemented using pictured numeric output words. Consequently,
     * its use may corrupt the transient region identified by #>.
     */

    fun w_question(vm: ForthVM) {
        val v = vm.mem[vm.dstk.pop()]
        vm.io.print(v.toString(vm.base.coerceIn(2, 36)) + " ")
    }

    /** DUMP     TOOLS   ( addr u -- )
     *
     * Display the contents of u consecutive addresses starting at addr. The
     * format of the display is implementation dependent.
     *
     * DUMP may be implemented using pictured numeric output words.
     * Consequently, its use may corrupt the transient region identified by #>.
     */

    fun w_dump(vm: ForthVM) {
        val len: Int = vm.dstk.pop()
        val start: Int = vm.dstk.pop()
        val end = start + len - 1
        var i = start - (start % 4)
        while (i < start + len) {
            val a = if (i >= start && i <= end) vm.mem[i].hex8
            else "        "

            val b = if (i + 1 >= start && i + 1 <= end) vm.mem[i+1].hex8
            else "        "

            val c = if (i + 2 >= start && i + 2 <= end) vm.mem[i+2].hex8
            else "        "

            val d = if (i + 3 >= start && i + 3 <= end) vm.mem[i+3].hex8
            else "        "

            vm.io.println("${i.addr} = $a $b $c $d")
            i += 4
        }
    }

    /** SEE  TOOLS   ( "<spaces>name" -- )
     *
     * Display a human-readable representation of the named word's definition.
     * The source of the representation (object-code decompilation, source
     * block, etc.) and the particular form of the display is implementation
     * defined.
     *
     * SEE may be implemented using pictured numeric output words.
     * Consequently, its use may corrupt the transient region identified by #>.
     */

    fun w_see(vm: ForthVM) {
        val w: Word = vm.dict[vm.getToken()]
        wToolsExtra._see(vm, w, false)
    }

}