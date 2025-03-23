package kf.words.doublenums

import kf.IMetaWordModule
import kf.IWordModule

object mDoubleNums: IMetaWordModule {
    override val name = "kf.words.doublenums.mDoubleNums"
    override val description = "Double words"
    override val modules = arrayOf<IWordModule>(
        wDoubleNums,
    )
}