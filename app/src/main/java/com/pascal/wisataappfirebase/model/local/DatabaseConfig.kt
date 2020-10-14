package com.pascal.wisataappfirebase.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pascal.wisataappfirebase.model.local.user.DaoUser
import com.pascal.wisataappfirebase.model.local.user.User
import com.pascal.wisataappfirebase.model.local.wisata.DaoWisata
import com.pascal.wisataappfirebase.model.local.wisata.Wisata

@Database(entities = arrayOf(Wisata::class, User::class), version = 1, exportSchema = false)
abstract class DatabaseConfig: RoomDatabase() {

    abstract fun wisataDao(): DaoWisata
    abstract fun userDao(): DaoUser

    companion object {
        private var INSTANCE: DatabaseConfig? = null

        fun getInstance(context: Context): DatabaseConfig? {
            if (INSTANCE == null) {
                synchronized(DatabaseConfig::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseConfig::class.java, "dbsiswa.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }

            return INSTANCE
        }
    }
}