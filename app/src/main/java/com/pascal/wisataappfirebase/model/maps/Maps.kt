package com.pascal.wisataappfirebase.model.maps

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Maps (
     var lat: String? = null,
     var lon: String? = null
) : Parcelable