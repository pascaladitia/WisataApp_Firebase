package com.pascal.wisataappfirebase.model.local.user

import androidx.room.*

@Dao
interface DaoUser {

    @Query("SELECT * FROM user")
    fun getData(): List<User>

    @Query("SELECT * FROM user where email = :email")
    fun getDataEmail(email: String) : User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("DELETE FROM user")
    fun delete()
}