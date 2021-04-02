package com.example.roomlivedataexample.db

import androidx.lifecycle.LiveData
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import com.example.summerholidays.network.model.ImageFile
import kotlinx.coroutines.CoroutineScope

interface IDBRepository {

    suspend fun saveImage(url: String , filePath: String): ImageFile?
    suspend fun deleteImage(item: ImageFile) : Int
    suspend fun deleteAll() : Int
    suspend fun getImage(item: ImageItem): ImageFile?
    fun getListImage(scope: CoroutineScope): LiveData<List<ImageFile>>
}
