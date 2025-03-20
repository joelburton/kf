package kf.words.core

import kf.IWordClass
import kf.Word
import kf.words.core.ext.wNumIOExt

object mCore: IWordClass {
    override val name = "core.Core"
    override val description = "Core words"
    override val words = arrayOf<Word>(
        *wChars.words,
        *wComments.words,
        *wCompiling.words,
        *wCreate.words,
        *wDoubleCell.words,
        *wFormatting.words,
        *wFunctions.words,
        *wIfThen.words,
        *wInterp.words,
        *wIO.words,
        *wLogic.words,
        *wLoops.words,
        *wMath.words,
        *wMemory.words,
        *wNumIO.words,
        *wParsing.words,
        *wRStack.words,
        *wStackOps.words,
        *wStrings.words,
        *wSystem.words,
        *wUnsigned.words,
        *wVariables.words,
        *wWords.words
    )
}