package com.pascal.wisataappfirebase.model.online

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WisataFirebase (
    var image: String? = null,
    var name: String? = null,
    var description: String? = null,
    var location: String? = null,
    var lat: String? = null,
    var lon: String? = null,
    var key: String? = null
) : Parcelable {
    constructor(): this( "", "", "", "", "", "", "")
}