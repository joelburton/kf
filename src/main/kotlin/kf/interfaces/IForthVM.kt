package kf.interfaces

import kf.dict.Word
import kotlin.time.TimeSource

const val TRUE: Int = -1
const val FALSE: Int = 0

interface IForthVM {
    /** [kf.interfaces.IConsole] for system. */
    val io: IConsole

    /** [kf.interfaces.IInterp] for this VM */
    val interp: IInterp

    /** [kf.interfaces.IMemConfig] for this VM */
    val memConfig: IMemConfig

    /** RAM for the VM. */
    val mem: IntArray

    /** Base of math output (10 is for decimal and default) */
    var base: Int

    /** Verbosity of system:
     *
     * -  4  very low-level debugging
     * -  3  show internal msgs
     * -  2  show all words
     * -  1  welcome messages, user warnings, etc. (default)
     * -  0  no banner or unimportant warnings
     * - -1  quiet: no warnings
     * - -2  very no prompt, no output except direct (like .)
     *
     *  To see any dev-debugging messages, the system needs to
     *  be compiled for it..
     */
    var verbosity: Int

    /** Current end of the CODE section. */
    var cend: Int

    /** Current end of the DATA section. */
    var dend: Int

    /** Start of the CODE section. This comes from the memory configuration
     * passed in, and normally wouldn't ever change. However, to hack around,
     * users *can* change this.
     */
    var cstart: Int

    /** Start of the DATA section. See [cstart]. */
    var dstart: Int

    /** The pointer the scanner is at in a line of input. */
    var inPtr: Int

    /**  Word dictionary. */
    val dict: IDict

    /**  This is only needed to make see/simple-see commands more helpful. */
    val cellMeta: Array<ICellMeta>

    /** Data stack (the one normally used by end users) */
    val dstk: IFStack

    /** Return stack (for calling/returning from fn calls */
    val rstk: IFStack

    /** Current word being executed by the VM. */
    var currentWord: Word
    var ip: Int

    /** Stack of input sources, with the top being the active one. */
    val sources: ArrayList<ISource>

    /** Convenient way to get the currently-active input source (or null) */
    val source: ISource

    /** Time mark for when VM started (the `millis` word reports # of millis
     * since server start, since it's not possible to return millis before the
     * 1970 epoch on a 32-bit machine.)
     */
    val timeMarkCreated: TimeSource.Monotonic.ValueTimeMark

    /** Quit: what `QUIT` calls --- go to interactive interp and reset.
     *
     * The name "QUIT" is traditional in forth and tied to the QUIT word,
     * but it doesn't really mean "quit the VM" or "quit the interpreter" or
     * such. It means, basically, "quit reading any file or evaluating this
     * line and go back to interactive mode and read a new line."
     *
     * The word to quit the interpreter is BYE.
     * */
    fun quit()

    /** Abort: this what `abort` does as well as any forth error */
    fun abort()

    /** Programmers get lonely and we love to get logs. */
    fun dbg(lvl: Int, s: String)
}
