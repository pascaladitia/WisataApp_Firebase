package com.pascal.wisataappfirebase.model.local.wisata

import androidx.room.*

@Dao
interface DaoWisata {

    @Query("SELECT * FROM wisata")
    fun getData(): List<Wisata>

    @Insert
    fun insert(wisata: Wisata)

    @Update
    fun update(wisata: Wisata)

    @Delete
    fun delete(wisata: Wisata)
}