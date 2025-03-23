package kf.words.tools

import kf.IMetaWordModule

object mTools: IMetaWordModule {
    override val name = "Tools"
    override val description = "Tools"
    override val modules = arrayOf(
        wTools,
        wToolsExt,
    )
}