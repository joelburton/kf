package kf.words.machine

import kf.dict.IWordMetaModule

object mMachine: IWordMetaModule {
    override val name = "kf.words.machine.mMachine"
    override val description = "Machine words"
    override val modules = arrayOf(
        wMachine,
        wMachineDebug,
    )
}