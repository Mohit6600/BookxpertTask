package com.example.bookxpert.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookxpert.data.local.dao.ApiItemDao
import com.example.bookxpert.data.local.model.LocalApiItem

@Database(entities = [LocalApiItem::class], version = 2, exportSchema = false)
abstract class ApiDatabase : RoomDatabase() {
    abstract fun apiItemDao(): ApiItemDao

    companion object {
        @Volatile
        private var INSTANCE: ApiDatabase? = null

        fun getDatabase(context: Context): ApiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApiDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
