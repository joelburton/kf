package kf.words.machine

import kf.IMetaWordModule

object mMachine: IMetaWordModule {
    override val name = "kf.words.machine.mMachine"
    override val description = "Machine words"
    override val modules = arrayOf(
        wMachine,
        wMachineDebug,
    )
}