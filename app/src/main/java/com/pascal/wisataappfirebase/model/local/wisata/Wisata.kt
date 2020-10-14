package com.pascal.wisataappfirebase.model.local.wisata

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "wisata")
data class Wisata (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "location")
    var location: String? = null,

    @ColumnInfo(name = "latitude")
    var latitude: String? = null,

    @ColumnInfo(name = "Longtitude")
    var longtitude: String? = null
): Parcelable