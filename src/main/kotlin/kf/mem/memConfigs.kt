package kf.mem


object smallMemConfig : MemConfig(
    name = "Small (1k)",
    regsStart = 0x0000,
    regsEnd = 0x000f,
    scratchStart = 0x0010,
    scratchEnd = 0x002f,
    padStart = 0x0030,
    padEnd = 0x00ff,
    codeStart = 0x100,
    codeEnd = 0x01ff,
    dataStart = 0x0200,
    dataEnd = 0x027f,
    interpBufStart = 0x0280,
    interpBufEnd = 0x02ff,
    dstackStart = 0x0300,
    dstackEnd = 0x037f,
    rstackStart = 0x0380,
    rstackEnd = 0x03ff,
    upperBound = 0x03ff,
)


object medMemConfig : MemConfig(
    name = "Medium (16k)",
    regsStart = 0x0000,
    regsEnd = 0x000f,
    scratchStart = 0x0010,
    scratchEnd = 0x002f,
    padStart = 0x0030,
    padEnd = 0x01ff,
    codeStart = 0x200,
    codeEnd = 0x17ff,
    dataStart = 0x1800,
    dataEnd = 0x2dff,
    interpBufStart = 0x2e00,
    interpBufEnd = 0x2fff,
    dstackStart = 0x3000,
    dstackEnd = 0x37ff,
    rstackStart = 0x3800,
    rstackEnd = 0x3fff,
    upperBound = 0x3fff,
)

object largeMemConfig : MemConfig(
    name = "Large (64k)",
    regsStart = 0x0000,
    regsEnd = 0x000f,
    scratchStart = 0x0010,
    scratchEnd = 0x002f,
    padStart = 0x0030,
    padEnd = 0x01ff,
    codeStart = 0x200,
    codeEnd = 0x4fff,
    dataStart = 0x5000,
    dataEnd = 0x9fff,
    interpBufStart = 0xa000,
    interpBufEnd = 0xafff,
    dstackStart = 0xb000,
    dstackEnd = 0xefff,
    rstackStart = 0xf000,
    rstackEnd = 0xffff,
    upperBound = 0xffff,
)

val memConfigs = arrayOf(smallMemConfig, medMemConfig, largeMemConfig)