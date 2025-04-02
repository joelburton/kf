package kf.words.facility

import kf.interfaces.IWordMetaModule

object mFacility: IWordMetaModule {
    override val name = "kf.words.facility.mFacility"
    override val description = "Facility words"
    override val modules = arrayOf(
        wFacility,
        wFacilityExt,
    )
}