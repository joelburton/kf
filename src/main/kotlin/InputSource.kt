/** The Forth VM can take input from different sources:
 *
 * - interactive: a real person at a keyboard
 * - reading from a file at startup
 * - reading from a file by `INCLUDE` or similar things
 * - evaluating a simple string
 * - getting strings from the network, when acting as a gateway
 *
 * Inputs can be "nested": file A can read file B which itself can read C.
 *
 * When a ForthVM is instantiated, it won't have a source at all.
 * However, during the initial reboot, it will have a StdInputSource added
 * as the first on its stack of input sources (it needs to be rebooted to use
 * it; it is also rebooted by `COLD`)
 *
 * When there are start up files to read, each of those are added at the
 * top of that stack of inputs. When a file is INCLUDED, or EVALUATE is used,
 * another input source is added to the stack.
 *
 * In the VM, `.source` always returns the top item on the stack.
 *
 * Each input source contains the pointer to where it is in the content
 * (on a line-by-line basis), as well as the scanner for that input (which
 * itself tracks what has been parsed in the line). This allows for things
 * like:
 *
 *    INCLUDE a.fth ." STILL GOT HERE"
 *
 * that is, once a.fth is done (plus any children it reads), it will still
 * be able to re-place the pointer to the current line of input in this source.
 *
 * The StdIn source is always ID=0. There is a FakeStdIn source for testing,
 * and that also uses ID=0. Any `EVALUATE` frame always gets -1 as the id;
 * there can be several of these nested (for people who are really crazy
 * and want to put EVALUATE inside EVALUATE ;-) ).
 *
 * Otherwise, it gets an id that refers to the file being read.
 */


package kf

import com.github.ajalt.mordant.platform.MultiplatformSystem

/** ABC of all input sources. */

abstract class InputSource(val vm: ForthVM, val id: Int, val path: String) {
    /** For sources like file-reading, what char (0 is first) are we at? */
    var ptr: Int = 0

    /** For all sources, what "line number" are we at.
     *
     * For interactive sources, this is "what number prompt are we acting at?"
     * These are 1-based, as lines in files are.
     */
    var lineCount: Int = 0

    /** Copy stashed away of the scanner's pointer to where it's tokenized.
     *
     * In theory, this could just be a property of the scanner instance.
     * However, ANS Forth requires us to have a word `>IN` that returns an
     * address of a cell with this info, and it can be changed for users to
     * change where the scanner is. Therefore, it needs to be stored in a
     * spot in RAM. As such, we need to tuck away where this way when a child
     * source is about to pushed over us (since it will have its own >IN,
     * overwriting ours. When that child is popped, this can be restored.
     * */
    var storedInPtr: Int = 0

    abstract fun readLineOrNull(): String?

    /** Used in error messages, like '<0:stdin>:42` or `4:<foo.fth>:17` */
    override fun toString() = "$id:$path:$lineCount"

    /**  Scanner for reading and tokenizing input line. */
    var scanner: FScanner = FScanner(vm)

    /** Push a new input source on top of this one.
     *
     * Store on us the current in-line-scanner pointer (see above).
     */
    fun push(newSrc: InputSource) {  // fixme: can this just be this?
        if (vm.sources.isNotEmpty()) vm.source.storedInPtr = vm.inPtr
        vm.sources.add(newSrc)
        vm.inPtr = 0
    }

    /** Pop this input source off stack and get ready to use source below us.
     *
     * If this is the last item on the stack:
     * - in normal modes, the program will quit
     * - in tests, though, we will catch this EOF
     *
     * As a dying act, this source will set the >IN ptr to what the stack
     * below us had it at, and will resume the VM at the start of the
     * interp loop.
     */

    fun pop() {
        vm.sources.removeLast()
        if (vm.sources.isEmpty()) throw IntEOF()
        vm.inPtr = vm.source.storedInPtr
        vm.ip = vm.cstart
    }
}

/** Input source for files (either read-at-start or INCLUDE-d.
 *
 * Since it's legal for Forth users to change where they are in an input
 * source (using SAVE-SOURCE, changing info, and RESTORE-SOURCE, plus to help
 * with compatability of this outside of stuff like JVM, I'm choosing to read
 * the entire file at a gulp and "moving around in the file" is just
 * adjusting a pointer of where are directly, rather than fiddling around
 * with system-specific syscalls like tell, etc.
 * */

open class FileInputSource(
    vm: ForthVM, id: Int, path: String
) : InputSource(vm, id, path) {
    // slurp, slurp, slurp, that's tasty unicode
    open val content: String =
        MultiplatformSystem.readFileAsUtf8(path).toString()

    /** Read line and return null at end of file. */

    override fun readLineOrNull(): String? {
        if (ptr >= content.length) return null
        var s = String()
        while (ptr < content.length && content[ptr++] != '\n') {
            s += content[ptr - 1]
        }
        lineCount += 1
        return s
    }
}

/** Interactive stdin source. */

class StdInInputSource(vm: ForthVM) : InputSource(vm, 0, "<stdin>") {

    /** Get a line from a real console or something piped in via the shell.
     *
     * Returns NULL at end of pipe or when console user uses Control-D
     * (or Control-Z for any Forth-loving Windows users)
     * */

    override fun readLineOrNull(): String? {
        lineCount += 1
        return readlnOrNull()
    }
}

/** A simple input source for EVALUATE: it's given a string that's its input. */

class EvalInputSource(
    vm: ForthVM, override val content: String
) : FileInputSource(vm, -1, "<eval>")

/** Input source for tests: the content can be added to.
 *
 * Since the tests often feed multiple lines in at different points in a test,
 * the "input" here is a string that can be reset from the user.
 */

class FakeStdInInputSource(
    vm: ForthVM, override var content: String
) : FileInputSource(vm, 0, "<fake>")