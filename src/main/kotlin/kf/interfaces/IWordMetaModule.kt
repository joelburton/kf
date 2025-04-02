package kf.interfaces

/** A class that brings together several modules.
 *
 * For example, the ANS core words are divided into many files (wLoops,
 * wIfThen, etc); however, there is mCore that itself includes all of those.
 */

interface IWordMetaModule {
    val name: String
    val description: String
    val modules: Array<IWordModule>
}
