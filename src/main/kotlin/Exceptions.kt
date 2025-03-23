package kf

/** Interpreter hit a problem.
 *
 * This is sealed, so you can't instantiate it directly. Instead, there are
 * subclasses for the different types.
 */
sealed class ForthError(m: String) : RuntimeException(m)

/**  Might be an invalid number or missing word. */
class ParseError(m: String) : ForthError("Parse error: $m")

/** Invalid char. */
class CharLitError(m: String) : ForthError(m)

/** Using compile-only word in interpreter, etc. */
class InvalidState(m: String) : ForthError("Invalid state: $m")

/** Couldn't read a file for inclusion. */
class FileError(m: String) : ForthError("File error: $m")

/** Memory overrun or invalid address. */
class MemError(m: String) : ForthError("Memory error: $m")

/** A word is in the dictionary, but not yet really defined. */
class WordNotImplemented(m: String="Word not implemented") : ForthError(m)

/** Division by zero is invalid in our math. */
class DivisionByZero(m: String="Division by zero") : ForthError(m)

/** Problem loading a binary word module. */
class BloadError(m: String) : ForthError("Bload error: $m")

/** Number outside of our range (32-bit signed) */
class NumOutOfRange(m: Long) : ForthError("Number out of range: $m")

/** Buffer overruns, etc. */
class ForthBufferError(m: String) : ForthError(m)

/** Input/output problem */
class ForthIOError(m: String) : ForthError(m)

/** Use of deferred word */
class ForthDeferredWordError(m: String) : ForthError(m)

/** Word length */
class WordLengthError(m: String) : ForthError(m)

/** Base class of "interruptions".
 *
 * These are things that might stop the interpreter or quit the program.
 * Each is handled differently: some are caught in the interpreter loop,
 * others are larger than that and caught higher up.
 */
sealed class ForthInterrupt(m: String) : RuntimeException(m)

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
class IntEOF(m: String="EOF") : ForthInterrupt(m)

/** Stop processing this file.
 *
 * This is thrown by the `\\\` word (nonstandard: Gforth). It causes
 * the system to move onto the next file (or to interactive use).
 */
class IntQuitNonInteractive(m:String="\\\\\\") : ForthInterrupt(m)

/** Crash VM.
 *
 * The `BRK` word throws this, so the user can crash the system.
 * This is helpful with debugging a Forth program, since a debugger
 * can catch this.
 */
class IntBrk(m: String="BRK") : ForthInterrupt(m) {
    init {
        // Always print stack trace, even w/low verbosity or handled elsewhere.
        this.printStackTrace()
    }
}

/** Reset interpreter and return to interactive mode.
 *
 * - EVALUATE: done eval-ing, just return to interactive
 *     (caught by EVALUATE)
 * - interactive mode: just return to start of interp routine
 * - file mode: stop reading ALL files, return to interactive
 * - gateway/test: treat like as in interactive mode
 */
class IntQuit(m: String="QUIT") : ForthInterrupt(m)

/** Quit the program.
 *
 * - except gateway/test: finish the cmds but don't stop server
 */
class IntBye(m: String="Bye") : ForthInterrupt(m)

/** Shutdown entire system.
 *
 * This is only needed for gateway/test mode as a command that
 * will actually shut the server down.
 */
class IntServerShutDown(m: String="Shutdown") : ForthInterrupt(m)

