package kf.words

import kf.interfaces.IWordMetaModule
import kf.interfaces.IWordModule
import kf.words.core.ext.wCompileExt
import kf.words.core.ext.wInterpExt
import kf.words.core.wComments
import kf.words.core.wFunctions
import kf.words.core.wIO
import kf.words.core.wIfThen
import kf.words.core.wInterp
import kf.words.core.wLogic
import kf.words.core.wLoops
import kf.words.core.wMemory
import kf.words.core.wNumIO
import kf.words.core.wParsing
import kf.words.core.wStackOps
import kf.words.core.wStrings
import kf.words.core.wWords
import kf.words.custom.wBload
import kf.words.custom.wInterpCustom
import kf.words.custom.wMemoryCustom
import kf.words.machine.wMachine
import kf.words.machine.wMachineDebug

object mForthInterp : IWordMetaModule {
    override val name = "kf.words.mForthInterp"
    override val description = "Modules required for classic interpreter"
    override val modules: Array<IWordModule> = arrayOf(
        wMachine,
        wMachineDebug,

        wBload,
        wComments,
        wFunctions,
        wIO,
        wIfThen,
        wInterp,
        wLogic,
        wLoops,
        wMemory,
        wNumIO,
        wParsing,
        wStackOps,
        wStrings,
        wWords,

        wCompileExt,
        wInterpExt,

        wInterpCustom,
        wMemoryCustom,
    )
}