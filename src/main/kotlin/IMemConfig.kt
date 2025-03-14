package kf

interface IMemConfig {
    val regsStart: Int;
    val regsEnd: Int;
    val codeStart: Int
    val codeEnd: Int
    val dataStart: Int
    val dataEnd: Int
    val dstackStart: Int
    val dstackEnd: Int
    val rstackStart: Int
    val rstackEnd: Int
    val lstackStart: Int
    val lstackEnd: Int
    val upperBound: Int
}

object SmallMemConfig : IMemConfig {
    override val regsStart: Int = 0x0000
    override val regsEnd: Int = 0x000f
    override val codeStart: Int = 0x0010
    override val codeEnd: Int = 0x01ff
    override val dataStart: Int = 0x0200
    override val dataEnd: Int = 0x02ff
    override val dstackStart: Int = 0x0300
    override val dstackEnd: Int = 0x03df
    override val rstackStart: Int = 0x03e0
    override val rstackEnd: Int = 0x03ef
    override val lstackStart: Int = 0x03f0
    override val lstackEnd: Int = 0x03ff
    override val upperBound: Int = 0x03ff
}

object MedMemConfig: IMemConfig {
    override val regsStart: Int = 0x0000
    override val regsEnd: Int = 0x000f
    override val codeStart: Int = 0x0010
    override val codeEnd: Int = 0x10ff
    override val dataStart: Int = 0x2000
    override val dataEnd: Int = 0x2fff
    override val dstackStart: Int = 0x3000
    override val dstackEnd: Int = 0x3dff
    override val rstackStart: Int = 0x3e00
    override val rstackEnd: Int = 0x3eff
    override val lstackStart: Int = 0x3f00
    override val lstackEnd: Int = 0x3fff
    override val upperBound: Int = 0x3fff
}

object LargeMemConfig: IMemConfig {
    override val regsStart: Int = 0x0000
    override val regsEnd: Int = 0x00ff
    override val codeStart: Int = 0x0100
    override val codeEnd: Int = 0x4fff
    override val dataStart: Int = 0x5000
    override val dataEnd: Int = 0xafff
    override val dstackStart: Int = 0xb000
    override val dstackEnd: Int = 0xefff
    override val rstackStart: Int = 0xf000
    override val rstackEnd: Int = 0xf7ff
    override val lstackStart: Int = 0xf800
    override val lstackEnd: Int = 0xffff
    override val upperBound: Int = 0xffff
}

