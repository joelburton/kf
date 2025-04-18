package kf.words.core

import kf.interfaces.IWordMetaModule

object mCore: IWordMetaModule {
    override val name = "kf.words.core.mCore"
    override val description = "Core words"
    override val modules = arrayOf(
        wChars,
        wComments,
        wCompiling,
        wCreate,
        wDoubleCell,
        wFormatting,
        wFunctions,
        wIfThen,
        wInterp,
        wIO,
        wLogic,
        wLoops,
        wMath,
        wMemory,
        wNumIO,
        wParsing,
        wStacks,
        wStackOps,
        wStrings,
        wSystem,
        wUnsigned,
        wVariables,
    )
}