package kf.words.doublenums

import kf.dict.IWordMetaModule
import kf.dict.IWordModule

object mDoubleNums: IWordMetaModule {
    override val name = "kf.words.doublenums.mDoubleNums"
    override val description = "Double words"
    override val modules = arrayOf<IWordModule>(
        wDoubleNums,
    )
}