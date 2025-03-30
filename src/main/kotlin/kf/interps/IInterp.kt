package kf.interps

import kf.ForthVM
import kf.dict.IWordMetaModule

/** Interface for any "interpreter".
 *
 * The VM needs an interpreter, but some of the subclasses can be very
 * bare-bones and not have any CLI for interacting with the system, like
 * embedded Forth systems used to.
 */

interface IInterp {
    var vm: ForthVM
    fun setUp(vm: ForthVM)

    /** Name of interpreter. Purely informational. */
    val name: String

    /** What VM will call when reset (ie, QUIT/ABORT) is called. */
    fun reset()

    /** What VM will call to get interpreter code put in memory. */
    fun addInterpreterCode()

    /** What the interpreter needs to do after a reboot ("COLD" or startup) */
    fun reboot()

    /** State of interpreter. (See below for values) */
    var state: Int

    /** Convenience function for checking the state */
    val isInterpreting: Boolean
    /** Convenience function for checking the state */
    val isCompiling: Boolean

    /** Function to print out starting banner on VM reboot. */
    fun banner()

    /** Compile a string-token into CODE section. */
    fun compile(token: String)

    /** Execute a string token in the interpreter. */
    fun interpret(token: String)

    /** What module includes all the words needed by this interpreter?
     *
     * This way, if the user reboots with COLD-RAW or the `--raw` option,
     * they'll still get enough to run the interpreter.
     */
    val module: IWordMetaModule
}
