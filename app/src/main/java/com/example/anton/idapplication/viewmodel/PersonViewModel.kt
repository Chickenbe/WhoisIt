package com.example.anton.idapplication.viewmodel

import android.app.Application
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

    //this code will run when the object initialize
    init {
        val personDao = AppDatabase.getDatabase(application, viewModelScope).personDao()        //creating an object of database
        repository = PersonRepository(personDao)                                //create repo for data store
        allPerson = repository.allPerson                                        //get a LiveData list of Person object
    }

    //coroutine for async query
    fun insert(person: Person) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(person)
    }

}