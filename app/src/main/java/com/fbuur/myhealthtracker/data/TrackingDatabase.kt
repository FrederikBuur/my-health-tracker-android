package com.fbuur.myhealthtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.data.model.TemplateType

@Database(
    entities = [Registration::class, Parameter.Note::class, Parameter.Slider::class,
        Template::class, TemplateType::class],
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