package com.example.anton.idapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Person::class], version = 5)
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
                )/*.fallbackToDestructiveMigration()*/
                    .addCallback(PersonDatabaseCallback(scope))
                    .build()
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
        }
    }
}