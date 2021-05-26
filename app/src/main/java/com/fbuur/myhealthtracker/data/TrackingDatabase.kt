package com.fbuur.myhealthtracker.data

import android.content.ContentValues
import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fbuur.myhealthtracker.data.model.*
import com.fbuur.myhealthtracker.data.registration.TrackingDAO
import com.fbuur.myhealthtracker.data.registration.TrackingTypeConverters
import java.util.*

@Database(
    entities = [
        Registration::class, Template::class, TemplateType::class,
        Parameter.Note::class, Parameter.Slider::class, Parameter.Number::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [TrackingTypeConverters::class])
abstract class TrackingDatabase : RoomDatabase() {

    abstract fun trackingDAO(): TrackingDAO

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
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            db.insert(
                                "template",
                                OnConflictStrategy.IGNORE,
                                addNoteTemplate()
                            )

                            db.insert(
                                "templatetype",
                                OnConflictStrategy.IGNORE,
                                addNoteParameterTemplate()
                            )

                        }
                    })
                    .build()
                INSTANCE = instance
                return instance
            }

        }

        // insert initial data
        private fun addNoteTemplate(): ContentValues {
            val values = ContentValues()
            values.put("id", -1L)
            values.put("name", "Note")
            values.put("lastUsed", Date().toString())
            values.put("color", "#BEB541")
            return values
        }

        private fun addNoteParameterTemplate(): ContentValues {
            val values = ContentValues()

            values.put("id", -1L)
            values.put("temId", -1L)
            values.put("type", ParameterType.NOTE.name)

            return values
        }

    }

}