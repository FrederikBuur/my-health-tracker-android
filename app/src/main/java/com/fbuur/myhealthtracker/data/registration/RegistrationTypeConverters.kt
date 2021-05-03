package com.fbuur.myhealthtracker.data.registration

import androidx.room.TypeConverter
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.data.model.RegistrationType
import java.util.*

class RegistrationTypeConverters {

    // Parameter type enum type converters
    @TypeConverter
    fun fromParameterType(value: ParameterType): Int{
        return value.ordinal
    }
    @TypeConverter
    fun toParameterType(value: Int): ParameterType{
        return ParameterType.values()[value]
    }

    // Registration type enum type converters
    @TypeConverter
    fun fromRegistrationType(value: RegistrationType): Int{
        return value.ordinal
    }
    @TypeConverter
    fun toRegistrationType(value: Int): RegistrationType{
        return RegistrationType.values()[value]
    }


    // Sealed class Parameter type converters
//    @TypeConverter
//    fun sealedClassToString(sealedClass: SealedClass) : String = GsonExtension.toJson(sealedClass)
//
//
//    @TypeConverter
//    fun sealedClassFromString(sealedClass: String) : SealedClass = sealedClass.let { GsonExtension.fromJson(it) }

    // Date type converters
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

}