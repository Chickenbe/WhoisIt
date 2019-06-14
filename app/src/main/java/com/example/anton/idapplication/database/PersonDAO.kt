package com.example.anton.idapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PersonDAO {

    @Insert
    suspend fun insertAll(vararg person: Person)

    @Insert
    suspend fun insert(person: Person)

    @Delete
    fun delete(person: Person)

    @Query("SELECT * FROM person_table ORDER BY second_name ASC")
    fun getAllPerson(): LiveData<List<Person>>

}