package com.example.summerholidays.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import com.example.summerholidays.database.DBHelper
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.utils.Constance
import com.example.summerholidays.utils.ImageFileManager
import kotlinx.coroutines.delay


object FileDownloadManager {

    suspend fun downloadImage(context: Context, imageItem: ImageItem): ImageFile? {
        val data: Bitmap
        return try {
            val response = ApiHelper.getPhoto(imageItem.raw)
            if (response.contentLength() > 0) {
                data = BitmapFactory.decodeStream(response.byteStream())
                val path = ImageFileManager.saveImage(context, data, Constance.FOLDER_NAME, imageItem.url)
                if (path != null) {
                    val result = DBHelper.saveImage(imageItem.url, path)
                    if (result != null) {
                        return result
                    } else {
                        null
                    }
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

//    suspend fun isDownloaded(imageItem: ImageItem): Boolean {
//
//        val listImageDB = DBFunction.getAllImage()
//        for (image in listImageDB) {
//            if (TextUtils.equals(image.url , imageItem.url)) {
//                return true
//            }
//        }
//        return false
//    }

//    suspend fun getAllListImage(): ArrayList<ImageFile> {
//
//        val listImageGallery = ArrayList<ImageFile>()
//        for (image in DBFunction.getAllImage()) {
//            listImageGallery.add(ImageFile(image.url , image.path))
//        }
//        return listImageGallery
//    }

//    suspend fun deleteImage(imageFile: ImageFile) {
//
//        DBHelper.deleteImageDB(item)
//        SaveImageFile.checkDeletedialog(viewModelScope, context, getUriByPosition(position), position)
//    }
//
//    suspend fun getImageByUrl(url: String): ImageFile {
//        return DBFunction.getImageByUrl(url)
//    }

}