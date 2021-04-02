package com.example.summerholidays.ui.home

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.*
import com.example.summerholidays.network.ApiHelper
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import com.example.rotrofitwithcoroutineexampletow.data.model.LoadingStatus
import com.example.summerholidays.database.DBHelper
import com.example.summerholidays.network.FileDownloadManager
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.utils.Util
import com.tapi.a0028speedtest.base.BaseAndroidViewModel

import kotlinx.coroutines.*

fun List<ImageFile>.isDownload(url: String): LoadingStatus {
    for (item in this) {
        if (TextUtils.equals(item.url, url)) return LoadingStatus.LOADONE
    }
    return LoadingStatus.IDLE
}

fun List<ImageItem>.sysDownloadStatus(listDB: List<ImageFile>): List<ImageItem> {
    for (item in this) {
        item.downloaded = listDB.isDownload(item.url)
    }
    return this
}

enum class DownloadImageStatus {
    DOWNLOAD_SUCCESSFUL, DOWNLOAD_FAIL
}

enum class NetworkStatus {
    NETWORK_ERROR, INTERNET_ERROR, INTERNET_ERROR_FRIST, INTERNET_SUCCESSFUL
}

class HomeViewModel(application: Application) : BaseAndroidViewModel(application) {

    val TAG = "giangtd"
    private val context = getApplication<Application>().applicationContext
    private var _networkData = MediatorLiveData<List<ImageItem>>()
    val networkData: LiveData<List<ImageItem>> get() = _networkData

    private val listImages = ArrayList<ImageItem>()
    private val listDB = ArrayList<ImageFile>()

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _exceptionNetwork = MutableLiveData<NetworkStatus>()
    val exceptionNetWork: LiveData<NetworkStatus> get() = _exceptionNetwork

    private val _downloadImageStatus = MutableLiveData<DownloadImageStatus>()
    val downloadImageStatus: LiveData<DownloadImageStatus> get() = _downloadImageStatus

    private var isLoadMore: Boolean = true
    private var pageLoadMore = 1

    private var isAutoLoadData = true

    init {
        _networkData.addSource(DBHelper.getListImage(viewModelScope)) {
            listDB.clear()
            listDB.addAll(it)
            var index = 0
            for (item in listImages) {
                val newItem = item.copy()
                if (item.downloaded != LoadingStatus.LOADING) {
                    newItem.downloaded = listDB.isDownload(item.url)
                }
                listImages.set(index, newItem)
                index++
            }
            _networkData.value = listImages
        }

        if (!Util.isNetworkConnected(context)) {
            if (listImages.isEmpty()) {
                _exceptionNetwork.value = NetworkStatus.INTERNET_ERROR_FRIST
                _loading.value = false
            }
        }
    }

    fun getData() {
        Log.d(TAG, "getData: pageLoadMore : " + pageLoadMore)
        if (Util.isNetworkConnected(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                isLoadMore = false
                withContext(Dispatchers.Main) {
                    _loading.value = true
                }
                val listImage = ApiHelper.getListPhoto(pageLoadMore)
                if (listImage != null) {
                    withContext(Dispatchers.Main) {
                        if (listDB.size > 0) {
                            listImage.sysDownloadStatus(listDB)
                            listImages.addAll(listImage)
                        } else {
                            listImages.addAll(listImage)
                        }
                        Log.d(TAG, "getData: listImages.size : " + listImages.size)
                        _networkData.value = listImages
                        _loading.value = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _exceptionNetwork.value = NetworkStatus.NETWORK_ERROR
                        _exceptionNetwork.value = null
                        _loading.value = false
                    }
                }
                isLoadMore = true
                isAutoLoadData = false
                pageLoadMore++
            }
            if (listImages.isEmpty()) {
                _exceptionNetwork.value = NetworkStatus.INTERNET_SUCCESSFUL
            }
        } else {
            if (listImages.isEmpty()) {
                _exceptionNetwork.value = NetworkStatus.INTERNET_ERROR_FRIST
                _loading.value = false
            } else {
                _exceptionNetwork.value = NetworkStatus.INTERNET_ERROR
                _exceptionNetwork.value = null
            }
        }
    }

    fun downloadImage(image: ImageItem) {
        if (Util.isNetworkConnected(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                var index = 0
                var newItem: ImageItem = ImageItem("", "", "", LoadingStatus.IDLE)
                for (item in listImages) {
                    if (TextUtils.equals(item.url, image.url)) {
                        newItem = item.copy()
                        break
                    }
                    index++
                }

                if (newItem.clicked) {
                    return@launch
                }

                newItem.downloaded = LoadingStatus.LOADING
                newItem.clicked = true
                listImages.set(index, newItem)
                withContext(Dispatchers.Main) {
                    _networkData.value = listImages
                }

                val newItem2 = newItem.copy()
                if (FileDownloadManager.downloadImage(context, image) != null) {

                    newItem2.downloaded = LoadingStatus.LOADONE
                    newItem2.clicked = false
                    listImages.set(index, newItem2)
                    withContext(Dispatchers.Main) {
                        _networkData.value = listImages
                        _downloadImageStatus.value = DownloadImageStatus.DOWNLOAD_SUCCESSFUL
                        _downloadImageStatus.value = null
                        Log.d(TAG, "downloadImage: 1111")
                    }
                } else {
                    newItem2.downloaded = LoadingStatus.IDLE
                    newItem2.clicked = false
                    listImages.set(index, newItem2)
                    withContext(Dispatchers.Main) {
                        _downloadImageStatus.value = DownloadImageStatus.DOWNLOAD_FAIL
                        _downloadImageStatus.value = null
                        _networkData.value = listImages
                    }
                }
            }
        } else {
            _exceptionNetwork.value = NetworkStatus.INTERNET_ERROR
            _exceptionNetwork.value = null
        }
    }

    fun getListSize() = listImages.size
    fun getLoadMoreStatus() = isLoadMore
    fun getAutoLoadData() = isAutoLoadData


}