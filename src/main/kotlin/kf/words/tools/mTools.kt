package kf.words.tools

import kf.dict.IWordMetaModule

object mTools: IWordMetaModule {
    override val name = "tools.mTools"
    override val description = "Tools"
    override val modules = arrayOf(
        wTools,
        wToolsExt,
    )
}