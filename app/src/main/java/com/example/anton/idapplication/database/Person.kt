package com.example.anton.idapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "person_table")
data class Person (
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "middle_name") val secondName: String?,
    @ColumnInfo(name = "second_name") val middleName: String?,
    @ColumnInfo(name = "birthday") val birthdayDate: String?,
    @ColumnInfo(name = "person_num") val personalNum: Int
) : Serializable {
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
    val fullName: String
        get() = "$middleName $firstName $secondName"
}