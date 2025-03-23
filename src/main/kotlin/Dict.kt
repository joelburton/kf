package kf

import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextColors.blue
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.Whitespace
import com.github.ajalt.mordant.terminal.muted
import com.github.ajalt.mordant.widgets.Text

class WordNotFoundError(m: String) : ForthError("Word not found: $m")
class DictFullError() : ForthError("Dictionary full")

interface IWordModule {
    val name: String
    val description: String
    val words: Array<Word>
}

interface IMetaWordModule {
    val name: String
    val description: String
    val modules: Array<IWordModule>
}

// todo: should "words" be kept in reverse order?
// - that's always how we look for a word
// - plus, we'd have nice idiotmatic code for get, like:

//     words.first { it.name.equals(name, ignoreCash }

class Dict(val vm: ForthVM, val capacity: Int = 1024)  {
    private val _words = arrayListOf<Word>()
    val words: List<Word> = _words
    var currentlyDefining: Word? = null

    fun reset() {
        if (D) vm.dbg(3, "dict.reset")
        _words.clear()
        currentlyDefining = null
    }

    val size: Int get() = _words.size
    val last: Word get() = _words.last()

    operator fun get(wn: Int): Word {
        if (wn < 0 || wn >= _words.size) throw WordNotFoundError("$wn")
        return _words[wn]
    }

    operator fun get(name: String): Word {
        for (w in _words.asReversed()) {
            if (w.name.equals(name, ignoreCase = true)) return w
        }
        throw WordNotFoundError(name)
    }

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
    fun getByMem(n: Int): Word? {
        for (w in _words.reversed()) {
            if (w.dpos == n || w.cpos == n) return w
        }
        return null
    }

    /**  Get Word by name (null for not found)
     */
    fun getSafeChkRecursion(name: String?): Word? {
        for (w in _words.asReversed()) {
            if (w.name.equals(name, ignoreCase = true)) {
                if (currentlyDefining !== w || w.recursive) return w
                else vm.io.muted(
                    "Skipping currently-defining word because it isn't"
                            + " recursive"
                )
            }
        }
        return null
    }

    fun getNum(name: String): Int {
        for (i in _words.indices.reversed()) {
            if (_words[i].name.equals(name, ignoreCase = true)) return i
        }
        throw WordNotFoundError(name)
    }

    fun add(word: Word) {
        if (D) vm.dbg(3, "dict.add: ${word.name}")
        if (_words.size >= capacity) throw DictFullError()
        _words.add(word)
        word.wn = _words.lastIndex
    }

    fun addModule(mod: IWordModule, reloadOk: Boolean = false) {
        if (D) vm.dbg(3, "dict.addModule: ${mod.name}")

        if (!reloadOk && mod.name in vm.modulesLoaded) {
            vm.io.muted("Skipping already-loaded module: ${mod.name}")
        }

        if (vm.verbosity >= 1) vm.io.println(bold(yellow("${mod.name}:")))
        val sb = StringBuilder()
        for (w in mod.words) {
            add(w)
            if (vm.verbosity >= 1) sb.append("${w.name} ")
        }
        if (vm.verbosity >= 1)
            vm.io.println(Text("    $sb", whitespace= Whitespace.PRE_WRAP))

        vm.modulesLoaded.put(mod.name, mod)
    }

    fun addMetaModule(mod: IMetaWordModule) {
        if (D) vm.dbg(3, "dict.addMetaModule: ${mod.name}")
        if (vm.verbosity >= 1) vm.io.println(bold(blue("${mod.name}:")))
        for (m in mod.modules) addModule(m, true)
    }

    // ******************************************************* manipulating dict

    /**  Gets rid of all words at 'n' and after (including n).
     * Used for "w_forget"
     */
    fun truncateAt(n: Int) {
        if (D) vm.dbg(3, "dict.truncateAt $n")
        if (_words.size > n) {
            _words.subList(n, _words.size).clear()
        }
    }

    /**  Remove last word (being defined or already defined)
     */
    fun removeLast() {
        if (D) vm.dbg(3, "dict.removeLast")
        _words.removeLast()
    }

//    fun replaceWord(newWord: Word, wn: Int) {
//        _words[wn] = newWord
//    }
}