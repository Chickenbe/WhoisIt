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
    abstract fun personDao(): PersonDAO            //abstract function that returns PersonDAO object


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


        /*
        Callback function is using when the database created
        That function will delete all data in database when the app restart
        For async function call here is using coroutinesScope
        */
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

            personDao.insert(Person("Антон", "Михайлович", "Великоборец", "21.01.1999", 14523))
            personDao.insert(Person("Василий", "Андреевич", "Пашуто", "05.08.1995", 56478))
            personDao.insert(Person("Анна", "Анатольевич", "Зыбицкая", "19.10.2001", 75678))
            personDao.insert(Person("Артём", "Витальевич", "Грецкий", "01.03.1989", 34528))
        }
    }
}