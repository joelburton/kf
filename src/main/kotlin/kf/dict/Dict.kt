package kf.dict

import kf.D
import kf.DictError
import kf.ForthVM
import kf.wrap
import org.jline.utils.AttributedStyle

class WordNotFoundError(m: String) : DictError("Word not found: $m")
class DictFullError() : DictError("Dictionary full")

/** The dictionary of Forth words (sometimes called the "glossary".
 *
 * Every word is here, even though some words are very internal-facing and
 * generally hidden from users of the system. Words that are in the process
 * of being made are also added here.
 *
 * TODO: could we just not add a word until its complete?
 */

class Dict(val vm: ForthVM, val capacity: Int = 1024) {
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
    val words: List<Word> = _words

    /** The word, if any, that is currently being defined.
     *
     * This is cleared after a definition has succeeded or failed.
     */
    var currentlyDefining: Word? = null

    val size: Int get() = _words.size
    val last: Word get() = _words.last()

    /** Reset the entire dictionary after a system restart.
     *
     * Any new state added to the dictionary itself should be added here.
     * */

    fun reset() {
        if (D) vm.dbg(3, "dict.reset")
        _words.clear()
        currentlyDefining = null
    }

    /** Get word by wn, like ```dict[wn]```. Throws error if not found. */

    operator fun get(wn: Int): Word {
        if (wn < 0 || wn > _words.lastIndex) throw WordNotFoundError("$wn")
        return _words[wn]
    }

    /** Get word by name, like ```dict["dup"]```. Throws err if not found. */

    operator fun get(name: String): Word =
        _words.asReversed()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: throw WordNotFoundError(name)

    /** Get word by name, but returns null if word-not-found.
     * 
     * Almost always, trying to refer to a non-existant word should be an error,
     * but there are specific use cases, like `FIND`, etc, where a missing word
     * shouldn't be an error.
     */
    
    fun getSafe(name: String): Word? =
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
                    "Skipping currently-defining word because it isn't recursive")
            }
        }
    }

    /** Add a word to the dictionary. */

    fun add(word: Word) {
        if (D) vm.dbg(3, "dict.add: ${word.name}")
        if (_words.size >= capacity) throw DictFullError()
        _words.add(word)
        word.wn = _words.lastIndex
    }

    /** Add an entire module of words (like wMemory)
     *
     * Already-loaded modules are skipped unless `reloadOk` is true.
     */

    fun addModule(mod: IWordModule, reloadOk: Boolean = false) {
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
                vm.io.println("\n" + sb.wrap(vm.io.termWidth, indent=4))
        }

        vm.modulesLoaded.put(mod.name, mod)
    }

    /** Add a "meta-module", a module that just contains other modules. */

    fun addMetaModule(mod: IWordMetaModule) {
        val modName = mod.name.removePrefix("kf.words.")
        if (D) vm.dbg(3, "dict.addMetaModule: ${mod.name}")
        if (vm.verbosity >= 2) vm.io.success("$modName:")
        for (m in mod.modules) addModule(m, false)
    }

    /**  Gets rid of all words at 'n' and after (including n).
     *
     * Used for "w_forget"
     */

    fun truncateAt(n: Int) {
        if (D) vm.dbg(3, "dict.truncateAt $n")
        if (_words.size > n) {
            _words.subList(n, _words.size).clear()
        }
    }

    /**  Remove last word (being defined or already defined) */
    fun removeLast() {
        if (D) vm.dbg(3, "dict.removeLast")
        _words.removeLast()
    }
}