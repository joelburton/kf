package kf.sources

import kf.ForthVM
import java.nio.file.Files
import kotlin.io.path.Path

/** Input source for files (either read-at-start or INCLUDE-d.
 *
 * Since it's legal for Forth users to change where they are in an input
 * source (using SAVE-SOURCE, changing info, and RESTORE-SOURCE, plus to help
 * with compatability of this outside of stuff like JVM, I'm choosing to read
 * the entire file at a gulp and "moving around in the file" is just
 * adjusting a pointer of where are directly, rather than fiddling around
 * with system-specific system calls like tell, etc.
 * */

class SourceFile(
    vm: ForthVM, id: Int, path: String
) : SourceBaseString(vm, id, path) {
    // slurp, slurp, slurp, that's tasty unicode
    override val content= Files.readString(Path(path)).toString()
}
