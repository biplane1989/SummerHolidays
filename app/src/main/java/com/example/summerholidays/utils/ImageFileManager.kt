package com.example.summerholidays.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.summerholidays.ui.details.DetailViewModel
import com.example.summerholidays.ui.gallery.GalleryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

object ImageFileManager {

    fun checkExists(url: String): Boolean {
        val fCheck = File(url)
        return fCheck.exists()
    }

    fun checkExistsAndroidQ(context: Context, uri: Uri): Boolean {
        var cursor = context.getContentResolver().query(uri, null, null, null, null, null);
        try {
            return cursor != null && cursor.moveToFirst()
        } finally {
            cursor?.close()
        }
    }

    suspend fun deleteImage(context: Context, url: String): Boolean {
        val fdelete = File(url)
        if (fdelete.exists()) {
            return if (fdelete.delete()) {
                galleryAddPic(context, url)
                true
            } else {
                false
            }
        }
        return false
    }

    // add image into mediaStore
    fun galleryAddPic(context: Context, imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    fun getListImage(context: Context): ArrayList<String> {
        val images: ArrayList<String> = ArrayList<String>()
        images.clear()
        val uri: Uri
        val cursor: Cursor?
        var absolutePathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        cursor = context.contentResolver.query(uri, projection, null, null, "$orderBy DESC")
        while (cursor!!.moveToNext()) {
            absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            Log.e("Column", absolutePathOfImage)
            images.add(absolutePathOfImage)
        }

        return images
    }

    fun getListImageAndroidQ(context: Context): ArrayList<Uri> {
        val listUris = ArrayList<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN)
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder)
            ?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val dateTaken = Date(cursor.getLong(dateTakenColumn))
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    listUris.add(contentUri)

                }
            }
        return listUris
    }

    fun saveImage(context: Context, bitmap: Bitmap, folderName: String, name: String): String? {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                uri.let {
                    saveImageToStream(bitmap, context.contentResolver.openOutputStream(it))
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    context.contentResolver.update(it, values, null, null)
                }
                return uri.toString()
            } else {
                return null
            }
        } else {
            // Save image to gallery
            val savedImageURL = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, name, "Image of $name")
            if (savedImageURL != null) {
                return Uri.parse(savedImageURL.toString()).toString()
            } else {
                return null
            }
        }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkDeletedialog(scope: CoroutineScope, context: Context, url: String, viewModel: DetailViewModel, selectPositionImage: Int) {
        Log.d("TAG", "checkDeletedialog: url: " + url)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage("Are you sure,You wanted to delete")
        alertDialogBuilder.setPositiveButton("yes", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface?, arg1: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    scope.launch(Dispatchers.Default) {
                        if (deleteImageAndroidQ(context, url)) {
                            viewModel.deleteImage(selectPositionImage)
                        } else {
                            viewModel.deleteError()
                        }
                    }
                } else {
                    scope.launch(Dispatchers.Default) {
                        getRealPathFromURI(context, Uri.parse(url))?.let {
                            if (deleteImage(context, it)) {
                                viewModel.deleteImage(selectPositionImage)
                            } else {
                                viewModel.deleteError()
                            }
                        }
                    }
                }
            }
        })

        alertDialogBuilder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
//                finish()
            }
        })

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkDeleteAlldialog(scope: CoroutineScope, context: Context, urls: List<String>, viewModel: GalleryViewModel) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage("Are you sure,You wanted to delete")
        alertDialogBuilder.setPositiveButton("yes", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface?, arg1: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    scope.launch(Dispatchers.Default) {
                        var status = true
                        for (item in urls) {
                            if (!deleteImageAndroidQ(context, item)) {
                                status = false
                                break
                            }
                        }
                        if (status) {
                            viewModel.deleteAll()
                        } else {
                            viewModel.deleteError()
                        }
                    }
                } else {
                    scope.launch(Dispatchers.Default) {
                        var status = true
                        for (item in urls) {
                            val url = getRealPathFromURI(context, Uri.parse(item))
                            if (!deleteImage(context, url)) {
                                status = false
                                break
                            }
                        }
                        if (status) {
                            viewModel.deleteAll()
                        } else {
                            viewModel.deleteError()
                        }
                    }
                }
            }
        })

        alertDialogBuilder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
//                finish()
            }
        })

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteImageAndroidQ(context: Context, uri: String): Boolean {
        return try {
            context.contentResolver.delete(Uri.parse(uri), null, null)
            true
        } catch (e: RecoverableSecurityException) {
            val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                e.userAction.actionIntent.intentSender
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            intentSender?.let {
                ActivityCompat.startIntentSenderForResult(context as Activity, intentSender, Constance.DELETE_PERMISSION_REQUEST, null, 0, 0, 0, null)
            }
            false
        }
    }
}