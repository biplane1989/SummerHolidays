package com.example.summerholidays.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.summerholidays.database.entity.ImageEntity

@Dao interface ImageDAO {

    @Query("SELECT * FROM images WHERE url = :url")
    suspend fun getImage(url: String): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageEntity): Long

    @Query("SELECT * FROM images")
    fun getAll(): LiveData<List<ImageEntity>>

    @Query("DELETE from images where url = :url")
    suspend fun delete(url: String): Int
    
    @Query("DELETE FROM images")
    suspend fun deleteAllImage(): Int

    @Update suspend fun update(image: ImageEntity)

}