package kf.words.facility

import kf.IWordClass
import kf.Word

object mFacility: IWordClass {
    override val name = "Facility"
    override val description = "Facility words"
    override val words = arrayOf<Word>(
        *wFacility.words,
        *wFacilityExt.words,
    )
}