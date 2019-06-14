package com.example.anton.idapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Person::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDAO                              //abstract function that returns PersonDAO object


    //singletone, because we need only one object of database
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "person_db"
                ).addCallback(PersonDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }


        private class PersonDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.personDao())
                    }
                }
            }
        }


        suspend fun populateDatabase(personDao: PersonDAO) {
            personDao.deleteAll()

            var person =
                Person(
                    "Anton",
                    "Ivanovich",
                    "Velikoborets",
                    "21.10.1999",
                    123314
                )
            personDao.insert(person)
            person =
                Person("Jack", "Morovich", "Hawkins", "05.06.1996", 423137)
            personDao.insert(person)
        }
    }
}