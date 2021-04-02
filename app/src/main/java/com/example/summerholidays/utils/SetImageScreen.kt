package com.example.summerholidays.utils

import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.DisplayMetrics
import com.bumptech.glide.Glide
import com.example.summerholidays.network.model.ImageFile
import java.io.IOException

object SetImageScreen {

    var displayMetrics = DisplayMetrics()
    var width = 0
    var height: Int = 0

    fun setImageScreen(activity: Activity, context: Context, imageFile: ImageFile): Boolean {
        GetScreenWidthHeight(activity)

        val myWallpaperManager = WallpaperManager.getInstance(context.applicationContext)

        val image = Glide.with(context).asBitmap().load(imageFile.path).submit().get()
        val bitmap = Bitmap.createScaledBitmap(image, width, height, true)

        val newBitmap = returnBitmap(bitmap, width, height)

        return try {
            myWallpaperManager.setBitmap(newBitmap)

            myWallpaperManager.suggestDesiredDimensions(width, height)

            true
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            false
        }
    }

    private fun GetScreenWidthHeight(activity: Activity) {
        activity.windowManager.getDefaultDisplay().getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
    }

    private fun returnBitmap(originalImage: Bitmap, width: Int, height: Int): Bitmap? {
        val background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val originalWidth = originalImage.width.toFloat()
        val originalHeight = originalImage.height.toFloat()
        val canvas = Canvas(background)
        val scale = width / originalWidth
        val xTranslation = 0.0f
        val yTranslation = (height - originalHeight * scale) / 2.0f
        val transformation = Matrix()
        transformation.postTranslate(xTranslation, yTranslation)
        transformation.preScale(scale, scale)
        val paint = Paint()
        paint.setFilterBitmap(true)
        canvas.drawBitmap(originalImage, transformation, paint)
        return background
    }
}