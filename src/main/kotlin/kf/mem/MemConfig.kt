package kf.mem

import kf.addr

/** Sets of compatible choice for memory size and memory regions:
 *
 * - name: purely informational
 * - regs: registers; stuff like BASE and STATE and such
 * - scratch: scratch space used when bootstrapping and internally
 * - pad: scratch space that users can safely use
 * - code: definitions
 *   the start of this space is where the interp code (if any!) lives at
 * - data: miscellaneous data created by users (CREATE, ALLOT, etc.)
 * - interpBuf: memory scanner uses for line-in-process
 *   this is what SOURCE returns
 * - dStack: the data stack
 * - rStack: the return stack
 *
 * This system has intentionally no memory protection --- you can read/write
 * any address space in the machine. This makes for fun hacking, but you
 * can easily trash the stack or interpreter code or such.
 */

open class MemConfig(
    val name: String,
    val regsStart: Int,
    val regsEnd: Int,
    val scratchStart: Int, // mini-interpreter for bootstrapping
    val scratchEnd: Int,
    val padStart: Int, // user-facing pad
    val padEnd: Int,
    val codeStart: Int,
    val codeEnd: Int,
    val dataStart: Int,
    val dataEnd: Int,
    val interpBufStart: Int, // interp input buf
    val interpBufEnd: Int,
    val dstackStart: Int,
    val dstackEnd: Int,
    val rstackStart: Int,
    val rstackEnd: Int,
    val upperBound: Int,
) {
    fun show() {
        println("Mem start:     $0000-")
        println("Registers:     ${regsStart.addr}-${regsEnd.addr}")
        println("Scratchpad:    ${scratchStart.addr}-${scratchEnd.addr}")
        println("Pad:           ${padStart.addr}-${padEnd.addr}")
        println("Code:          ${codeStart.addr}-${codeEnd.addr}")
        println("Data:          ${dataStart.addr}-${dataEnd.addr}")
        println("Interp buffer: ${interpBufStart.addr}-${interpBufEnd.addr}")
        println("Dstack:        ${dstackStart.addr}-${dstackEnd.addr}")
        println("Rstack:        ${rstackStart.addr}-${rstackEnd.addr}")
        println("Upper bound:        -${upperBound.addr}")
    }

    companion object {
        val configs = arrayOf(smallMemConfig, medMemConfig, largeMemConfig)
    }
}
