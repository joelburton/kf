package kf.words

import kf.interfaces.IWordMetaModule
import kf.interfaces.IWordModule
import kf.words.machine.wMachine
import kf.words.machine.wMachineDebug

object mBaseInterp: IWordMetaModule {
    override val name = "kf.words.mBaseInterp"
    override val description = "Modules required for base interpreter"
    override val modules: Array<IWordModule> = arrayOf(
        wMachine,
        wMachineDebug,
    )
}