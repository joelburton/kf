package kf.words

import kf.interfaces.IWordMetaModule
import kf.interfaces.IWordModule
import kf.words.core.ext.wInterpExt
import kf.words.core.wLoops
import kf.words.core.wStackOps
import kf.words.custom.wBload
import kf.words.custom.wInterpCustom
import kf.words.machine.wMachine
import kf.words.machine.wMachineDebug

object mFastInterp: IWordMetaModule {
    override val name = "kf.words.mFastInterp"
    override val description = "Modules required for fast interpreter"
    override val modules: Array<IWordModule> = arrayOf(
        wMachine,
        wMachineDebug,

        wBload,
        wLoops,
        wStackOps,
        wInterpExt,

        wInterpCustom,
    )
}