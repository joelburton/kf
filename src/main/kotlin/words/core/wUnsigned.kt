package kf.words.core

import kf.IWordModule
import kf.Word
import kf.words.core.wDoubleCell.w_mStar
import kf.words.core.wLogic.w_lessThan
import kf.words.core.wMath.w_fmSlashMod
import kf.words.core.wNumIO.w_dot

object wUnsigned: IWordModule {
    override val name = "kf.words.core.wUnsigned"
    override val description = "Unsigned math"

    // We don't really support unsigned numbers (the JVM is really focused
    // on signed ints. Being 32-bit machine, it's unlikely that people will
    // want bigger numbers than can fit in 31-bits.
    //
    // There are unsigned ints for Kotlin; maybe I'll investigate these.

    override val words
        get() = arrayOf(
            Word("U.", ::w_dot),
            Word("UM*", ::w_mStar),
            Word("UM/MOD", ::w_fmSlashMod),
            Word("U<", ::w_lessThan),
        )
}