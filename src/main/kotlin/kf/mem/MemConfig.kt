package kf.mem

import kf.addr
import kf.interfaces.IMemConfig

open class MemConfig (
    override val name: String,
    override val regsStart: Int,
    override val regsEnd: Int,
    override val scratchStart: Int, // mini-interpreter for bootstrapping
    override val scratchEnd: Int,
    override val padStart: Int, // user-facing pad
    override val padEnd: Int,
    override val codeStart: Int,
    override val codeEnd: Int,
    override val dataStart: Int,
    override val dataEnd: Int,
    override val interpBufStart: Int, // interp input buf
    override val interpBufEnd: Int,
    override val dstackStart: Int,
    override val dstackEnd: Int,
    override val rstackStart: Int,
    override val rstackEnd: Int,
    override val upperBound: Int,
) : IMemConfig {
    override fun show() {
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
}
