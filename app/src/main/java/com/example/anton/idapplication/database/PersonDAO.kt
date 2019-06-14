package com.example.anton.idapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PersonDAO {

    @Insert
    suspend fun insertAll(vararg person: Person)

    @Insert
    suspend fun insert(person: Person)

    @Query("DELETE FROM person_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM person_table ORDER BY second_name ASC")
    fun getAllPerson(): LiveData<List<Person>>

}