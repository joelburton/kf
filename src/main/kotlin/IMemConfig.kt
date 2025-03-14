package kf

interface IMemConfig {
    val name: String;
    val regsStart: Int;
    val regsEnd: Int;
    val codeStart: Int
    val codeEnd: Int
    val dataStart: Int
    val dataEnd: Int
    val interpBufferStart: Int
    val interpBufferEnd: Int
    val dstackStart: Int
    val dstackEnd: Int
    val rstackStart: Int
    val rstackEnd: Int
    val lstackStart: Int
    val lstackEnd: Int
    val upperBound: Int

    fun show() {
        println("Registers:     ${regsStart.addr}-${regsEnd.addr}")
        println("Code:          ${codeStart.addr}-${codeEnd.addr}")
        println("Data:          ${dataStart.addr}-${dataEnd.addr}")
        println("Interp buffer: ${dstackStart.addr}-${dstackEnd.addr}")
        println("Dstack:        ${dstackStart.addr}-${dstackEnd.addr}")
        println("Rstack:        ${rstackStart.addr}-${rstackEnd.addr}")
        println("Lstack:        ${lstackStart.addr}-${lstackEnd.addr}")
        println("Upper bound:   ${upperBound.addr}")
    }
}

object SmallMemConfig : IMemConfig {
    override val name: String = "Small (1k)"
    override val regsStart: Int = 0x0000
    override val regsEnd: Int = 0x000f
    override val codeStart: Int = 0x0010
    override val codeEnd: Int = 0x01ff
    override val dataStart: Int = 0x0200
    override val dataEnd: Int = 0x027f
    override val interpBufferStart: Int = 0x0280
    override val interpBufferEnd: Int = 0x02ff
    override val dstackStart: Int = 0x0300
    override val dstackEnd: Int = 0x03df
    override val rstackStart: Int = 0x03e0
    override val rstackEnd: Int = 0x03ef
    override val lstackStart: Int = 0x03f0
    override val lstackEnd: Int = 0x03ff
    override val upperBound: Int = 0x03ff
}

object MedMemConfig: IMemConfig {
    override val name: String = "Medium (12k)"
    override val regsStart: Int = 0x0000
    override val regsEnd: Int = 0x000f
    override val codeStart: Int = 0x0010
    override val codeEnd: Int = 0x0fff
    override val dataStart: Int = 0x1000
    override val dataEnd: Int = 0x1f7f
    override val interpBufferStart: Int = 0x1f80
    override val interpBufferEnd: Int = 0x1fff
    override val dstackStart: Int = 0x2000
    override val dstackEnd: Int = 0x2dff
    override val rstackStart: Int = 0x2e00
    override val rstackEnd: Int = 0x2eff
    override val lstackStart: Int = 0x2f00
    override val lstackEnd: Int = 0x2fff
    override val upperBound: Int = 0x2fff
}

object LargeMemConfig: IMemConfig {
    override val name: String = "Large (64k)"
    override val regsStart: Int = 0x0000
    override val regsEnd: Int = 0x00ff
    override val codeStart: Int = 0x0100
    override val codeEnd: Int = 0x4fff
    override val dataStart: Int = 0x5000
    override val dataEnd: Int = 0xaeff
    override val interpBufferStart = 0xaf00
    override val interpBufferEnd: Int = 0xafff
    override val dstackStart: Int = 0xb000
    override val dstackEnd: Int = 0xefff
    override val rstackStart: Int = 0xf000
    override val rstackEnd: Int = 0xf7ff
    override val lstackStart: Int = 0xf800
    override val lstackEnd: Int = 0xffff
    override val upperBound: Int = 0xffff
}

val memoryConfigs = arrayOf(SmallMemConfig, MedMemConfig, LargeMemConfig)