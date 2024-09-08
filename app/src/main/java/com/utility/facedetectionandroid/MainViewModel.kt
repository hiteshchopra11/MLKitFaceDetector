package com.utility.facedetectionandroid

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utility.facedetectionandroid.utils.ImageUtil
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // Mutable state for image URI and bitmap
    var imageUri by mutableStateOf<Uri?>(null)
        private set

    var selectedBitmap by mutableStateOf<Bitmap?>(null)
        private set

    // Function to handle image URI update
    fun setImageUri(context: Context, uri: Uri?) {
        imageUri = uri
        // Load bitmap asynchronously
        viewModelScope.launch {
            selectedBitmap = uri?.let { ImageUtil.loadBitmapFromUri(context, it) }
        }
    }

    // Function to save the bitmap
    fun saveImageToStorage(context: Context, bitmap: Bitmap) {
        // Save image logic
        viewModelScope.launch {
            ImageUtil.saveImageToStorage(context, bitmap)
        }
    }
}
