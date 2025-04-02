package kf.words.doublenums

import kf.interfaces.IWordMetaModule
import kf.interfaces.IWordModule

object mDoubleNums: IWordMetaModule {
    override val name = "kf.words.doublenums.mDoubleNums"
    override val description = "Double words"
    override val modules = arrayOf<IWordModule>(
        wDoubleNums,
    )
}