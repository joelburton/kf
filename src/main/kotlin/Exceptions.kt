package kf

open class ForthError(msg: String) : Exception(msg)
class ForthMissingToken() : ForthError("Missing token")
class ForthWarning(msg: String) : ForthError(msg)

class ForthBrk(msg: String) : RuntimeException(msg)
class ForthQuit(msg: String) : RuntimeException(msg)
class ForthBye(msg: String) : RuntimeException(msg)
class ForthColdStop(msg: String) : RuntimeException(msg)

/**  End-of-file detected (exists current interpreter, both interactive and
 * non-interactive)
 */
class ForthEOF : RuntimeException()

/**  When raised, will quit the interpreter *if* this is a non-interactive
 * interpreter (like when reading a file with `include`)
 */
class ForthQuitNonInteractive : RuntimeException()

/**  Might be an invalid number or missing word.
 */
class ParseError(message: String) : ForthError("Parse error: $message")

class InvalidState(message: String) : ForthError("Invalid state: $message")
