package kf.dict

import kf.interfaces.IForthVM
import kf.interfaces.IWord



/** A Forth word.
 *
 * Two kinds of words:
 * - "primitives" (ie, coded in Kotlin)
 * - "colon words" (ie, in Forth)
 *
 * There are distinct sub-flavors:
 *
 * - CODE-word: a word which gets called and runs some code, like DUP or
 *   ` : my-word 42 . ; `
 *
 *   Primitives will have `.fn`, which is a method ref to the real Kotlin code.
 *   Colon words will have a `cpos`, the memory location of where their def
 *   begins.
 *
 * - DATA-word: a word which doesn't have a `cpos` but has a `dpos` -- the
 *   memory location of words that are pure-data, like what you get from
 *   `create foo` or `variable foo` (which is the same thing, really ;-) )
 *
 *   All words need a "what code should be run for this?", so the `.fn` for
 *   these words is a ref to the `(ADDR)` word function, which just puts the
 *   `dpos` of the word on the stack.
 *
 * - BOTH CODE and DATA: they have a dpos, pointing to the data location
 *   and a CPOS, pointing in memory to a colon-word fn to run. These are
 *   what `DOES>` gives you.
 */

class Word(
    /** Word name. Stored in lowercase, but lookup in case-insensitive. */
    name: String,

    /** Function to run; all words have one; pure-data words just use (ADDR) */
    override var fn: (IForthVM) -> Unit,

    /** Location of code-start for colon words with code. */
    override var cpos: Int = 0xffff,

    /** Location of data-start for Forth-created words with data. */
    override var dpos: Int = 0xffff,

    /** Should this word be hidden from casual users?
     *
     * Purely informational and doesn't affect functionality. IT's just nice
     * to not have stuff like the `WORDS` listing show very-internal and
     * uninteresting stuff.
     *
     * You can always see ALL words with `.DICT`
     */
    override var hidden: Boolean = false,

    /** Is this word IMMEDIATE (ie, executes in compilation mode) */
    override var imm: Boolean = false,

    /** Is this word compile-only?
     *
     * Words that make no sense not in a definition, or wouldn't work well
     * in one, like IF or ; are marked as such, so the system can throw a
     * helpful error if the user tries to use them.
     */
    override var compO: Boolean = false,

    /** Is word interpreter-only?
     *
     * These are words that shouldn't be used in a word definition and would
     * confuse the system. Users are given an error if they try to do so.
     */
    override var interpO: Boolean = false,

    /** Is this word marked as recursive?
     *
     * Words that are true here will still be found when the word is compiling
     * itself. In ANS Forth, you use the keyword `RECURSE` to call yourself,
     * and that has nothing to do with this field: this is to support the
     * nice GForth-style of `: a recursive a 42 a ;` to have A call itself. The
     * ANS-standard way of doing the same thing would be
     * `: a recurse 42 recurse ;`
     */
    override var recursive: Boolean = false,

    /** Does this word "defer" to another (if so, this is their word num)
     *
     * A deferred word is a word that doesn't have a definition now OR where
     * it should be able to changed later.
     *
     * Different from the standard: this support ANY word being able to be
     * deferred. That is both cool and breaks the rules.
     */
    override var deferToWn: Int? = null,

    /** The unique, sequential, unchanging word number.
     *
     * Since user code creates words and I don't want to spread responsibility
     * for "get the next available number before adding it throughout the
     * codebase, words can be created without a word number and, one second
     * after they're added to the dictionary, their number can be set on them.
     * Hence, "var" not "val". However, these definitely SHOULD NEVER BE
     * changed: everything will break.
     *
     * I could make this therefore nullable, but then lots of code will
     * need to have silly null-safety-checks.
     *
     * Somewhere Tony Hoare is smiling.
     * */
    override var wn: Int = 0 // look ma, not null ;-)
) : IWord {

    // fixme: add back
//    init {
//        if (name.length > 32) throw WordLengthError("Word name too long: $name")
//    }

    // Everything works if this is set to uppercase or not change the case;
    // all lookups for the words are done case-insensitively.
    override val name = name.lowercase()

    override fun toString() = name

    /** What executing word does: this is what `word()` runs */

//    override operator fun invoke(vm: IForthVM) {
//        vm.currentWord = this
//        if (D) vm.dbg(2, "x@ ${(vm.ip - 1).addr} -> $name ${getFnName()}")
//        fn(vm)
//        if (D) vm.dbg(3, "x@ ${vm.ip.addr} <- $name")
//    }





    // not currently using, but keeping it around in case its useful

//    fun isSameExec(other: Word): Boolean =
//        this.fn == other.fn
//                && this.cpos == other.cpos
//                && this.dpos == other.dpos
}