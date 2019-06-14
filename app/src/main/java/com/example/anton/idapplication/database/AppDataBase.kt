package com.example.anton.idapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Person::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDAO                              //abstract function that returns PersonDAO object


    //singletone, because we need only one object of database
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "person_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}