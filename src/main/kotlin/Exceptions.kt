package kf

/** Interpreter hit a problem.
 *
 * This is the base of all normal errors; this includes things like
 * word-not-found, etc. These are caught and cause a reset.
 */
open class ForthError(m: String) : Exception(m)

class ForthWarning(m: String) : ForthError(m)

/**  Might be an invalid number or missing word. */
class ParseError(m: String) : ForthError("Parse error: $m")

/** Using compile-only word in interpreter, etc. */
class InvalidState(m: String) : ForthError("Invalid state: $m")

/** Couldn't read a file for inclusion. */
class FileError(m: String) : ForthError("File error: $m")


// *******************************************************************
//
// These errors are exceptional, and signal stuff like "quit the
// program" or "stop reading this file". These are caught in different
// places depending on the mode.

sealed class Interrupt(m: String) : RuntimeException(m)

/**  EOF detected:
 *
 * This is tricky; it's caught in different places depending on the
 * mode.
 *
 * - evaluate (`EVALUATE`): finished evaluating, return
 * - normal interactive mode: quit program
 * - file or include: move to next file, then interactive
 * - gateway/test: done with this set of commands
 */
class IntEOF(m: String="EOF") : Interrupt(m)

/** Stop processing this file.
 *
 * This is thrown by the `\\\` word (nonstandard: Gforth). It causes
 * the system to move onto the next file (or to interactive use).
 */
class IntQuitNonInteractive(m:String="\\\\\\") : Interrupt(m)

/** Crash VM.
 *
 * The `BRK` word throws this, so the user can crash the system.
 * This is helpful with debugging a Forth program, since a debugger
 * can catch this.
 */
class IntBrk(m: String="BRK") : Interrupt(m)

/** Reset interpreter and return to interactive mode.
 *
 * - EVALUATE: done eval-ing, just return to interactive
 *     (caught by EVALUATE)
 * - interactive mode: just return to start of interp routine
 * - file mode: stop reading ALL files, return to interactive
 * - gateway/test: treat like as in interactive mode
 */
class IntQuit(m: String="QUIT") : Interrupt(m)

/** Quit the program.
 *
 * - except gateway/test: finish the cmds but don't stop server
 */
class IntBye(m: String="Bye") : Interrupt(m)

/** Shutdown entire system.
 *
 * This is only needed for gateway/test mode as a command that
 * will actually shut the server down.
 */
class IntServerShutDown(m: String="Shutdown") : Interrupt(m)
