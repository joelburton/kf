package kf.words

import kf.IMetaWordModule
import kf.IWordModule
import kf.words.core.wInterp
import kf.words.core.wStackOps
import kf.words.custom.wInterpCustom
import kf.words.machine.wMachine
import kf.words.machine.wMachineDebug

object mEvalInterp: IMetaWordModule {
    override val name = "kf.words.mEvalInterp"
    override val description = "Modules required for eval interpreter"
    override val modules: Array<IWordModule> = arrayOf(
        wMachine,
        wMachineDebug,
        wInterp,
        wStackOps,
        wInterpCustom,
    )
}