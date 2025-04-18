package kf.dict

import kf.D
import kf.DictError
import kf.ForthVM
import kf.WordLengthError
import kf.addr

import kf.interfaces.IDict
import kf.interfaces.IForthVM
import kf.interfaces.IWordMetaModule
import kf.interfaces.IWordModule
import kf.wrap

class WordNotFoundError(m: String) : DictError("Word not found: $m")
class DictFullError() : DictError("Dictionary full")

//  TODO: could we just not add a word until its complete?

class Dict(
    val vm: ForthVM,
    val capacity: Int = 1024
) :
    IDict {
    /** The real array of words, private to this class.
     *
     * It is arranged in order such that the word with wn=1 will be at array
     * index 1 (therefore, this list will only ever be truncated from the
     * end, never mutated in any other way).
     *
     * A read-only view is provided to outside users.
     */

    private val _words = arrayListOf<Word>()

    /** View for other classes.
     *
     * This is intended to be immutable --- but, unfortunately, it's still
     * possible for a badly-behaving other class to mutate it by casting it
     * back to its immutable form, like:
     *
     *   (words as ArrayList).add(...)
     *
     * If I was worried about this, I wouldn't expose this; I would have a
     * getter that returns a .toList() version of it, which would be an
     * entirely separate list, so any attempts at mutating it would be ignored.
     * That would entail copy it each time it's needed, which would have some
     * overhead. It isn't needed for this project.
     *
     */
    override val words: List<Word> = _words

    override var currentlyDefining: Word? = null

    override val size: Int get() = _words.size
    override val last: Word get() = _words.last()

    /** Reset the entire dictionary after a system restart.
     *
     * Any new state added to the dictionary itself should be added here.
     * */

    internal fun reset() {
        if (D) vm.dbg(3, "dict.reset")
        _words.clear()
        currentlyDefining = null
    }

    override operator fun get(wn: Int): Word {
        if (wn < 0 || wn > _words.lastIndex) throw WordNotFoundError("$wn")
        return _words[wn]
    }

    override operator fun get(name: String): Word =
        _words.asReversed()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: throw WordNotFoundError(name)

    override fun getSafe(name: String): Word? =
        _words.asReversed().find { it.name.equals(name, ignoreCase = true) }

    /**  Get word for if it uses this address.
     *
     * For example:
     * : a ;     (might be in code at 0x10, so getByMem(10) finds it)
     * create x  (might be in data at 0x200, so getByMem(200) find it)
     *
     * This is useful for w_see and w_simple-see, to show what word a memory
     * location might be linked to.
     */
    fun getByMem(n: Int): Word? =
        _words.asReversed().find { it.dpos == n || it.cpos == n }

    /**  Get word by name (null for not found)
     *
     * Normally, words cannot call themselves until they've finished
     * being added to the system.
     *
     * This is handled in the typical cases by ignoring the currently-def word
     * until it is completed and then using this method for the compilation
     * code.
     *
     * It will skip the currently-defined word unless it is marked as recursive
     * with the `RECURSIVE` extension.
     **
     * */

    fun getSafeChkRecursion(name: String?): Word? {
        return _words.asReversed().firstOrNull {
            it.name.equals(name, ignoreCase = true) &&
                    (currentlyDefining !== it || it.recursive)
        }?.also {
            if (currentlyDefining === it && !it.recursive) {
                vm.io.warning(
                    "Skipping currently-defining word because it isn't recursive"
                )
            }
        }
    }

    /** Add a word to the dictionary. */

    override fun add(word: Word) {
        if (D) vm.dbg(3, "dict.add: ${word.name}")
        if (_words.size >= capacity) throw DictFullError()
        if (word.name.length > 32)
            throw WordLengthError("Word name too long: ${word.name}")
        _words.add(word)
        word.wn = _words.lastIndex
    }

    /** Add an entire module of words (like wMemory)
     *
     * Already-loaded modules are skipped unless `reloadOk` is true.
     */

    internal fun addModule(mod: IWordModule, reloadOk: Boolean = false) {
        if (D) vm.dbg(3, "dict.addModule: ${mod.name}")

        if (!reloadOk && mod.name in vm.modulesLoaded) {
            if (vm.verbosity >= 2)
                vm.io.warning("Skipping already-loaded module: ${mod.name}")
            return
        }

        val modName = mod.name.removePrefix("kf.words.")
        if (vm.verbosity >= 2) vm.io.bold("  $modName: ")
        val sb = mod.words.joinToString(" ") {
            add(it)
            it.name.removePrefix("kf.words.")
        }
        if (vm.verbosity >= 2) {
            if (mod.name.length + sb.length + 3 < vm.io.termWidth)
                vm.io.println(sb)
            else
                vm.io.println("\n" + sb.wrap(vm.io.termWidth, indent = 4))
        }

        vm.modulesLoaded.put(mod.name, mod)
    }

    /** Add a "meta-module", a module that just contains other modules. */

    internal fun addMetaModule(mod: IWordMetaModule) {
        val modName = mod.name.removePrefix("kf.words.")
        if (D) vm.dbg(3, "dict.addMetaModule: ${mod.name}")
        if (vm.verbosity >= 2) vm.io.success("$modName:")
        for (m in mod.modules) addModule(m, false)
    }

    override fun truncateAt(n: Int) {
        if (D) vm.dbg(3, "dict.truncateAt $n")
        if (_words.size > n) {
            _words.subList(n, _words.size).clear()
        }
    }

    /**  Remove last word (being defined or already defined) */
    internal fun removeLast() {
        if (D) vm.dbg(3, "dict.removeLast")
        _words.removeLast()
    }

    /**  Useful for debugging and to support `w_see` and `w_simple-see`. */
    fun getHeaderStr(w: Word): String {
        return java.lang.String.format(
            "%s %-36s %-2s %-2s %-2s %-2s %-2s C:%-5s D:%-5s",
            String.format("(%3d)", w.wn),
            w.name,
            if (w.imm) "IM" else "",
            if (w.compO) "CO" else "",
            if (w.interpO) "IO" else "",
            if (w.recursive) "RE" else "",
            if (w.hidden) "HI" else "",
            if (w.cpos != NO_ADDR) w.cpos.addr else "",
            if (w.dpos != NO_ADDR) w.dpos.addr else ""
        )
    }

    // Purely internal: normalize a function name to remove noisy cruft.
    // This appears in debugging logs to show the function name:
    internal fun getFnName(w: Word): String {
        return w.fn.toString()
            .removeSuffix("(kf.ForthVM): kotlin.Unit")
            .removePrefix("fun ")
    }

    companion object {

        // A word to put in place where needed when "no word" is a value.
        fun noWordFn(vm: IForthVM) {
            vm.io.warning("No Word Fn ran")
        }

        val noWord = Word("noWord", ::noWordFn, hidden = true)

        /**  Explanation for header strings */
        const val HEADER_STR: String =
            " IMmediate Compile-Only Interp-Only REcurse HIdden Code Data"
    }


}