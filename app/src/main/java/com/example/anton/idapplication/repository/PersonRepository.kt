package com.example.anton.idapplication.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.anton.idapplication.database.Person
import com.example.anton.idapplication.database.PersonDAO

class PersonRepository(private val personDao: PersonDAO) {

    //set LiveData list of Person from data table in database
    val allPerson: LiveData<List<Person>> = personDao.getAllPerson()

    //async function for insert a data into database
    @WorkerThread
    suspend fun insert(person: Person) {
        personDao.insert(person)
    }

    @WorkerThread
    suspend fun deletePerson(person: Person) {
        personDao.deletePerson(person)
    }

}