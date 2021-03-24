package com.fbuur.myhealthtracker.data

import android.content.ContentValues
import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.data.model.TemplateType
import java.util.*

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
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val values = ContentValues()
                            values.put("id", -1L)
                            values.put("name", "Note")
                            values.put("lastUsed", Date().toString())
                            values.put("color", "#BEB541")

                            db.insert(
                                "template",
                                OnConflictStrategy.IGNORE,
                                values
                            )
                        }
                    })
                    .build()
                INSTANCE = instance
                return instance
            }

        }


    }

}