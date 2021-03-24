package com.fbuur.myhealthtracker.data

import androidx.room.TypeConverter
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.data.model.RegistrationType
import com.google.gson.Gson
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

    // Parameter type list type converters
//    @TypeConverter
//    fun fromParameterTypeList(list: List<ParameterType>): String {
//        return Gson().toJson(list)
//    }
//    @TypeConverter
//    fun toParameterTypeList(value: String): List<ParameterType> {
//        return Gson().fromJson(value, Array<ParameterType>::class.java).toList()
//    }

    // Date type converters
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Parameter type converters
//    @TypeConverter
//    fun fromParameter(parameter: Parameter): String {
//        return Gson().toJson(parameter)
//    }
//    @TypeConverter
//    fun toParameter(value: String): Parameter {
//        return Gson().fromJson(value, Parameter::class.java)
//    }

    // Parameter list type converters
//    @TypeConverter
//    fun fromParameterList(list: List<Parameter>): String {
//        return Gson().toJson(list)
//    }
//    @TypeConverter
//    fun toParameterList(value: String): List<Parameter> {
//        return Gson().fromJson(value, Array<Parameter>::class.java).toList()
//    }

}