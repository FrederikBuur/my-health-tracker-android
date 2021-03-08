package com.fbuur.myhealthtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Registration::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [RegistrationTypeConverters::class])
abstract class TrackingDatabase : RoomDatabase() {

    abstract fun registrationDao(): RegistrationDAO

    companion object {
        @Volatile
        private var INSTANCE: TrackingDatabase? = null


        fun getTrackingDatabase(context: Context): TrackingDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackingDatabase::class.java,
                    "tracking_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }


    }

}