package kf

class WordNotFoundException(msg: String) : Exception(msg)
class DictFullError() : Exception("Dictionary full")

interface WordClass {
    val name: String
    val primitives: Array<Word>
}

class Dict(val vm: ForthVM, val capacity: Int = 1024)  {
    private val words = arrayListOf<Word>()
    var currentlyDefining: Word? = null

    fun reset() {
        if (D) vm.dbg(3, "dict.reset")
        words.clear()
        currentlyDefining = null
    }

    val size: Int get() = words.size
    val last: Word get() = words.last()

    operator fun get(wn: Int): Word {
        if (wn < 0 || wn >= words.size) throw WordNotFoundException("${wn}")
        return words[wn]
    }

    operator fun get(name: String): Word {
        for (w in words.asReversed()) {
            if (w.name.equals(name, ignoreCase = true)) return w
        }
        throw WordNotFoundException(name)
    }

    fun getSafe(Name: String): Word? =
        words.asReversed().find { it.name.equals(Name, ignoreCase = true) }

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
        for (w in words.reversed()) {
            if (w.dpos == n || w.cpos == n) return w
        }
        return null
    }

    /**  Get Word by name (null for not found)
     */
    fun getSafeChkRecursion(name: String?, io: IOBase): Word? {
        for (w in words.asReversed()) {
            if (w.name.equals(name, ignoreCase = true)) {
                if (currentlyDefining !== w || w.recursive) return w
                else io.quiet(
                    "Skipping currently-defining word because it isn't"
                            + " recursive"
                )
            }
        }
        return null
    }

    fun getNum(name: String): Int {
        for (i in words.indices.reversed()) {
            if (words[i].name.equals(name, ignoreCase = true)) return i
        }
        throw WordNotFoundException(name)
    }

    fun add(word: Word) {
        if (D) vm.dbg(3, "dict.add: ${word.name}")
        if (words.size >= capacity) throw DictFullError()
        words.add(word)
        word.wn = words.size - 1
    }

    fun addModule(mod: WordClass) {
        if (D) vm.dbg(3, "dict.addModule: ${mod.name}")
        for (w in mod.primitives) add(w)
        vm.modulesLoaded.put(mod.name, mod)
    }

    // ******************************************************* manipulating dict

    /**  Gets rid of all words at 'n' and after (including n).
     * Used for "w_forget"
     */
    fun truncateAt(n: Int) {
        if (D) vm.dbg(3, "dict.truncateAt $n")
        if (words.size > n) {
            words.subList(n, words.size).clear()
        }
    }

    /**  Remove last word (being defined or already defined)
     */
    fun removeLast() {
        if (D) vm.dbg(3, "dict.removeLast")
        words.removeLast()
    }
}