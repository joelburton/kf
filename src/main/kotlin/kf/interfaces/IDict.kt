package kf.interfaces

import kf.dict.Word

/** The dictionary of Forth words (sometimes called the "glossary".
 *
 * Every word is here, even though some words are very internal-facing and
 * generally hidden from users of the system. Words that are in the process
 * of being made are also added here.
 */

interface IDict {
    /** List of [Word] instances in the dict. */
    val words: List<Word>

    /** The word, if any, that is currently being defined.
     *
     * This is cleared after a definition has succeeded or failed.
     */
    var currentlyDefining: Word?

    /** Number of words (including currently-defining */
    val size: Int

    /** The last word (which will also be the currently-defining word) */
    val last: Word

    /** Add a word to the dictionary. */
    fun add(word: Word)

    /** Get word by wn, like ```dict[wn]```. Throws error if not found. */
    operator fun get(wn: Int): Word

    /** Get word by name, like ```dict["dup"]```. Throws err if not found. */
    operator fun get(name: String): Word

    /** Get word by name, but returns null if word-not-found.
     *
     * Almost always, trying to refer to a non-existent word should be an error,
     * but there are specific use cases, like `FIND`, etc, where a missing word
     * shouldn't be an error.
     */
    fun getSafe(name: String): Word?

    /**  Gets rid of all words at 'n' and after (including n).
     *
     * Used for "w_forget"
     */
    fun truncateAt(n: Int)
}