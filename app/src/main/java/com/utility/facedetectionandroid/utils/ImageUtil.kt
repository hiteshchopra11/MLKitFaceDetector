package com.utility.facedetectionandroid.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtil {
    suspend fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()

        return (loader.execute(request) as? SuccessResult)?.drawable?.toBitmap()
    }

    fun saveImageToStorage(context: Context, bitmap: Bitmap) {
        val fileName = "IMG_${System.currentTimeMillis()}.png"
        val file = File(context.getExternalFilesDir(null), fileName)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(context, "Image saved at ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to get saved images from storage
    fun getSavedImagesFromStorage(context: Context): List<File> {
        val directory = context.getExternalFilesDir(null)
        return directory?.listFiles { file -> file.extension == "png" }?.toList() ?: emptyList()
    }

    // Function to load a bitmap from a file
    fun loadBitmapFromFile(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}