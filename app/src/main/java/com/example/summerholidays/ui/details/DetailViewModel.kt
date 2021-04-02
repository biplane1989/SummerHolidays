package com.example.summerholidays.ui.details

import android.app.Activity
import android.app.Application
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.summerholidays.database.DBHelper
import com.example.summerholidays.utils.SetImageScreen
import com.tapi.a0028speedtest.base.BaseAndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

enum class DetailNotification {
    DELETE_SUCCESSFUL, DELETE_FAIL, SET_PHOTO_SCREEN_SUCCESSFUL, SET_PHOTO_SCREEN_FAIL
}

class DetailViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    val listImage = DBHelper.getListImage(viewModelScope)

    private val _notificationDeleteAll = MutableLiveData<DetailNotification>()
    val notificationDeleteAll: LiveData<DetailNotification> get() = _notificationDeleteAll

    val empty = Transformations.map(listImage) {
        it.isNullOrEmpty()
    }

    fun deleteImage(position: Int) {
        viewModelScope.launch {
            val images = ArrayList(listImage.value)
            val item = images.get(position)
            withContext(Dispatchers.Main) {
                if (DBHelper.deleteImageDB(item) > -1) {
                    _notificationDeleteAll.value = DetailNotification.DELETE_SUCCESSFUL
                } else {
                    _notificationDeleteAll.value = DetailNotification.DELETE_FAIL
                }
            }
        }
    }

    fun getUriByPosition(position: Int): String {
        val images = ArrayList(listImage.value)
        return images.get(position).path
    }

    fun setBackgroundScreen(position: Int, activity: Activity) {
        viewModelScope.launch(Dispatchers.Default) {
            val images = ArrayList(listImage.value)
            val item = images.get(position)

            if (SetImageScreen.setImageScreen(activity, context, item)) {
                withContext(Dispatchers.Main) {
                    _notificationDeleteAll.value = DetailNotification.SET_PHOTO_SCREEN_SUCCESSFUL
                }
            } else {
                withContext(Dispatchers.Main) {
                    _notificationDeleteAll.value = DetailNotification.SET_PHOTO_SCREEN_FAIL
                }
            }
        }
    }

    fun deleteError() {
        _notificationDeleteAll.value = DetailNotification.DELETE_FAIL
    }
}