package com.example.anton.idapplication.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.anton.idapplication.database.AppDatabase
import com.example.anton.idapplication.database.Person
import com.example.anton.idapplication.repository.PersonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PersonRepository
    val allPerson: LiveData<List<Person>>
    private var context: Context

    //this code will run when the object initialize
    init {
        val personDao = AppDatabase.getDatabase(application, viewModelScope).personDao()        //creating an object of database
        repository = PersonRepository(personDao)                                //create repo for data store
        allPerson = repository.allPerson
        //get a LiveData list of Person object

        context = application.applicationContext
    }

    //coroutine for async query
    fun insert(person: Person) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(person)
    }

    fun deletePerson(person: Person) = viewModelScope.launch(Dispatchers.IO) {
        repository.deletePerson(person)
        context.deleteFile(person.pictureTag)
    }

}