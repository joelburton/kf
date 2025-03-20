package kf

interface IMemConfig {
    val name: String
    val regsStart: Int
    val regsEnd: Int
    val scratchStart: Int // mini-interpreter for bootstrapping
    val scratchEnd: Int
    val padStart: Int // user-facing pad
    val padEnd: Int
    val codeStart: Int
    val codeEnd: Int
    val dataStart: Int
    val dataEnd: Int
    val interBufStart: Int // interp input buf
    val interpBufEnd: Int
    val dstackStart: Int
    val dstackEnd: Int
    val rstackStart: Int
    val rstackEnd: Int
    val upperBound: Int

    fun show() {
        println("Mem start:     $0000-")
        println("Registers:     ${regsStart.addr}-${regsEnd.addr}")
        println("Scratchpad:    ${scratchStart.addr}-${scratchEnd.addr}")
        println("Pad:           ${padStart.addr}-${padEnd.addr}")
        println("Code:          ${codeStart.addr}-${codeEnd.addr}")
        println("Data:          ${dataStart.addr}-${dataEnd.addr}")
        println("Interp buffer: ${interBufStart.addr}-${interpBufEnd.addr}")
        println("Dstack:        ${dstackStart.addr}-${dstackEnd.addr}")
        println("Rstack:        ${rstackStart.addr}-${rstackEnd.addr}")
        println("Upper bound:        -${upperBound.addr}")
    }
}

open class SmallMemConfig : IMemConfig {
    override val name: String = "Small (1k)"
    override val regsStart: Int = 0x0000
    override val regsEnd: Int = 0x000f
    override val scratchStart: Int = 0x0010
    override val scratchEnd: Int = 0x001f
    override val padStart: Int = 0x0020
    override val padEnd: Int = 0x00ff
    override val codeStart: Int = 0x100
    override val codeEnd: Int = 0x01ff
    override val dataStart: Int = 0x0200
    override val dataEnd: Int = 0x027f
    override val interBufStart: Int = 0x0280
    override val interpBufEnd: Int = 0x02ff
    override val dstackStart: Int = 0x0300
    override val dstackEnd: Int = 0x03df
    override val rstackStart: Int = 0x03e0
    override val rstackEnd: Int = 0x03ff
    override val upperBound: Int = 0x03ff
}

open class MedMemConfig : SmallMemConfig() {
    override val name: String = "Medium (16k)"
    override val codeEnd: Int = 0x17ff
    override val dataStart: Int = 0x1800
    override val dataEnd: Int = 0x2eff
    override val interBufStart: Int = 0x2f00
    override val interpBufEnd: Int = 0x2fff
    override val dstackStart: Int = 0x3000
    override val dstackEnd: Int = 0x3dff
    override val rstackStart: Int = 0x3e00
    override val rstackEnd: Int = 0x3fff
    override val upperBound: Int = 0x3fff
}

class LargeMemConfig : MedMemConfig() {
    override val name: String = "Large (64k)"
    override val codeEnd: Int = 0x4fff
    override val dataStart: Int = 0x5000
    override val dataEnd: Int = 0xaeff
    override val interBufStart = 0xaf00
    override val interpBufEnd: Int = 0xafff
    override val dstackStart: Int = 0xb000
    override val dstackEnd: Int = 0xefff
    override val rstackStart: Int = 0xf000
    override val rstackEnd: Int = 0xffff
    override val upperBound: Int = 0xffff
}

val memoryConfigs = arrayOf(SmallMemConfig(), MedMemConfig(), LargeMemConfig())