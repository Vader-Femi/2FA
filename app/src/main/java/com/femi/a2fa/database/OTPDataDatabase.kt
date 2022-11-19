package com.femi.a2fa.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.femi.a2fa.database.dao.OTPDao
import com.femi.a2fa.database.entity.OTPData

@Database(entities = [OTPData::class], version = 1, exportSchema = false)
abstract class OTPDataDatabase : RoomDatabase() {

    abstract fun OTPDao(): OTPDao

    companion object {
        private const val databaseName = "2fa.database.OTPData"

        private var instance: OTPDataDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context,
            OTPDataDatabase::class.java,
            databaseName
        ).fallbackToDestructiveMigration()
            .build()
    }

}