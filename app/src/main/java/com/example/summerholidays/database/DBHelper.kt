package com.example.summerholidays.database

import androidx.lifecycle.LiveData
import com.example.roomlivedataexample.db.DBRepository
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import com.example.summerholidays.database.entity.ImageEntity
import com.example.summerholidays.network.model.ImageFile
import kotlinx.coroutines.CoroutineScope


object DBHelper {

    val dbRepository = DBRepository()
    suspend fun saveImage(url: String, filePath: String): ImageFile? {
        return dbRepository.saveImage(url, filePath)
    }

    fun getListImage(scope: CoroutineScope): LiveData<List<ImageFile>> {
        return dbRepository.getListImage(scope)
    }

    suspend fun deleteImageDB(item: ImageFile): Int {
        return dbRepository.deleteImage(item)
    }

    suspend fun getImage(item: ImageItem): ImageFile? {
        return dbRepository.getImage(item)
    }

    suspend fun deleteAll(): Int {
        return dbRepository.deleteAll()
    }
}