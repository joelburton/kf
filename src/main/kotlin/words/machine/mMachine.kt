package kf.words.machine

import kf.IWordClass
import kf.Word

object mMachine: IWordClass {
    override val name = "Machine"
    override val description = "Machine words"
    override val words = arrayOf<Word>(
        *wMachine.words,
        *wMachineDebug.words,
    )
}