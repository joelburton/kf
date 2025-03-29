package kf.words

import kf.dict.IWordMetaModule
import kf.dict.IWordModule
import kf.words.core.wInterp
import kf.words.core.wStackOps
import kf.words.custom.wInterpCustom
import kf.words.machine.wMachine
import kf.words.machine.wMachineDebug

object mEvalInterp: IWordMetaModule {
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