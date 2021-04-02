package com.example.summerholidays.ui.gallery

import androidx.lifecycle.*
import com.example.summerholidays.database.DBHelper
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.ui.details.DetailNotification
import com.example.summerholidays.ui.home.DownloadImageStatus
import com.example.summerholidays.utils.ImageFileManager
import com.tapi.a0028speedtest.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel : BaseViewModel() {

    private val _listImage = MediatorLiveData<List<ImageFile>>()
    val listImage: LiveData<List<ImageFile>> get() = _listImage

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _notificationDeleteAll = MutableLiveData<Boolean>()
    val notificationDeleteAll: LiveData<Boolean> get() = _notificationDeleteAll

    private val _downloadImageStatus = MutableLiveData<DownloadImageStatus>()
    val downloadImageStatus: LiveData<DownloadImageStatus> get() = _downloadImageStatus

    private var listSize = -1

    val empty = Transformations.map(listImage) {
        it.isNullOrEmpty()
    }

    init {
        _listImage.addSource(DBHelper.getListImage(viewModelScope)) {
            _listImage.value = it
            _loading.value = false
            if (listSize != -1) {
                if (listSize < it.size) {
                    _downloadImageStatus.value = DownloadImageStatus.DOWNLOAD_SUCCESSFUL
                    _downloadImageStatus.value = null
                }
                if (listSize == it.size) {
                    _downloadImageStatus.value = DownloadImageStatus.DOWNLOAD_FAIL
                    _downloadImageStatus.value = null
                }
            }
            listSize = it.size
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _notificationDeleteAll.value = DBHelper.deleteAll() > -1
            }
        }
    }

    fun getListUrl(): List<String> {
        val urls = ArrayList(_listImage.value)
        return urls.map { it.path }
    }

    fun deleteError() {
        _notificationDeleteAll.value = false
    }
}