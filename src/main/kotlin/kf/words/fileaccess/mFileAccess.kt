package kf.words.fileaccess

import kf.interfaces.IWordMetaModule

object mFileAccess: IWordMetaModule {
    override val name = "kf.words.fileaccess.mFileAccess"
    override val description = "File Access"
    override val modules = arrayOf(
        wFileAccess,
        wFileAccessExt,
    )
}