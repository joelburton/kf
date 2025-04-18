package kf.interfaces

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
interface ISource {
    /** Id of the source (-1=eval, 0=stdin, else=file#) */
    val id: Int

    /** Path to file or string to show for "location") */
    val path: String

    /** For sources like file-reading, what char (0 is first) are we at? */
    var ptr: Int

    /** For all sources, what "line number" are we at.
     *
     * For interactive sources, this is "what number prompt are we acting at?"
     * These are 1-based, as lines in files are.
     */
    var lineCount: Int

    /**  Scanner for reading and tokenizing input line. */
    val scanner: IFScanner

    fun readLineOrNull(): String?

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
    fun pop()

    /** Push a new input source on top of this one.
     *
     * Store on us the current in-line-scanner pointer (see above).
     */
    fun push(newSrc :ISource)
}