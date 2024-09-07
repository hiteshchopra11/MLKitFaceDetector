package com.utility.facedetectionandroid

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun FaceDetectionScreen(bitmap: Bitmap?) {
    var faces by remember { mutableStateOf<List<Face>>(emptyList()) }
    var bitmapWithFaces by remember { mutableStateOf(bitmap) }
    val scope = rememberCoroutineScope()

    // Start face detection and recompose only when bitmap instance changes
    LaunchedEffect(key1 = bitmap) {
        scope.launch(Dispatchers.IO) {
            faces = detectFaces(bitmap = bitmap)
            bitmapWithFaces = drawBoundingBoxesOnFaces(
                bitmap = bitmap,
                faces = faces
            )
        }
    }

    // Display the image with bounding boxes
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        bitmapWithFaces?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// Face detection function using ML Kit
suspend fun detectFaces(bitmap: Bitmap?): List<Face> {
    val image = bitmap?.let { InputImage.fromBitmap(it, 0) }

    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .build()

    val detector = FaceDetection.getClient(options)
    return try {
        if (image != null) {
            detector.process(image).await()
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// Draw bounding boxes on the detected faces
fun drawBoundingBoxesOnFaces(bitmap: Bitmap?, faces: List<Face>): Bitmap? {
    val outputBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = outputBitmap?.let { Canvas(it) }

    val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    for (face in faces) {
        val bounds: Rect = face.boundingBox
        canvas?.drawRect(bounds, paint)
    }

    return outputBitmap
}