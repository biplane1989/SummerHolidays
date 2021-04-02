package com.example.roomlivedataexample.db

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import com.example.summerholidays.database.ImageDatabase
import com.example.summerholidays.database.entity.ImageEntity
import com.example.summerholidays.network.model.ImageFile
import kotlinx.coroutines.*

class DBRepository : IDBRepository {

    override suspend fun saveImage(url: String, filePath: String): ImageFile? {
        val result = ImageDatabase.get().imageDao.insert(ImageEntity(0, url, filePath))
        return if (result > -1) {
            ImageFile(url, filePath)
        } else {
            null
        }
    }

    override suspend fun deleteImage(item: ImageFile): Int {
        return ImageDatabase.get().imageDao.delete(item.url)
    }

    override suspend fun deleteAll(): Int {
        return ImageDatabase.get().imageDao.deleteAllImage()
    }

    override suspend fun getImage(item: ImageItem): ImageFile? {
        val imageEntity = ImageDatabase.get().imageDao.getImage(item.url)
        if (imageEntity != null) {
            return ImageFile(url = imageEntity.url, path = imageEntity.path)
        } else {
            return null
        }
    }

//    override fun getListImage(scope: CoroutineScope): LiveData<List<ImageFile>> {
//        val result = MediatorLiveData<List<ImageFile>>()
//        result.addSource(ImageDatabase.get().imageDao.getAll()) {
//            Log.d("001", "getListImage: db size: " + it.size)
//            scope.launch(Dispatchers.Default) {
//                val data = it.map { ImageFile(it.url, it.path) }
//                withContext(Dispatchers.Main) {
//                    if (it != null) {
//                        result.value = data
//                    } else {
//                    }
//                }
//            }
//        }
//        return result
//    }

    override fun getListImage(scope: CoroutineScope): LiveData<List<ImageFile>> {
        return ImageDatabase.get().imageDao.getAll().map { it.map { ImageFile(it.url, it.path) } }
    }
}