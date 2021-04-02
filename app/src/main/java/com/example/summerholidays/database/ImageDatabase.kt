package com.example.summerholidays.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.summerholidays.database.dao.ImageDAO
import com.example.summerholidays.database.entity.ImageEntity


const val DATABASE_NAME = "image_db"

@Database(entities = [ImageEntity::class], exportSchema = false, version = 1)
abstract class ImageDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: ImageDatabase? = null
        fun create(context: Context) {
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        fun get(): ImageDatabase {
            return instance!!
        }

        private fun buildDatabase(context: Context): ImageDatabase {
            return Room.databaseBuilder(context, ImageDatabase::class.java, DATABASE_NAME).build()
        }
    }

    abstract val imageDao: ImageDAO
}