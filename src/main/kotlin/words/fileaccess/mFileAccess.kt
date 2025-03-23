package kf.words.fileaccess

import kf.IMetaWordModule

object mFileAccess: IMetaWordModule {
    override val name = "kf.words.fileaccess.mFileAccess"
    override val description = "File Access"
    override val modules = arrayOf(
        wFileAccess,
        wFileAccessExt,
    )
}