package kf.words.facility

import kf.IMetaWordModule

object mFacility: IMetaWordModule {
    override val name = "kf.words.facility.mFacility"
    override val description = "Facility words"
    override val modules = arrayOf(
        wFacility,
        wFacilityExt,
    )
}