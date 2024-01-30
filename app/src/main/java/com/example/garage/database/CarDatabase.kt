package com.example.garage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.garage.models.CarDb
import com.example.garage.models.NotificationDb

@Database(entities = [CarDb::class , NotificationDb::class], version = 2 , exportSchema = false)
abstract class CarDatabase: RoomDatabase() {
    abstract fun CarDao(): CarDao

    companion object {
        @Volatile
        private var INSTANCE: CarDatabase? = null

        fun getDatabase(context: Context): CarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, CarDatabase::class.java, "cars")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}