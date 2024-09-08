package com.utility.facedetectionandroid.screen.image_chooser.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.utility.facedetectionandroid.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun FaceDetectionUI(bitmap: Bitmap?, onSaveImage: (Bitmap) -> Unit) {
    val context = LocalContext.current
    var faces by remember { mutableStateOf<List<Face>>(emptyList()) }
    var bitmapWithFaces by remember { mutableStateOf(bitmap) }
    var selectedFace by remember { mutableStateOf<Face?>(null) }
    val scope = rememberCoroutineScope()

    // Map to store names for each face
    var faceNames by remember { mutableStateOf(mapOf<Int, String>()) }
    var showDialog by remember { mutableStateOf(false) }
    var enteredName by remember { mutableStateOf("") }

    // Start face detection and recompose only when bitmap instance changes
    LaunchedEffect(key1 = bitmap) {
        if (bitmap != null) {
            scope.launch(Dispatchers.IO) {
                val detectedFaces = detectFaces(bitmap)
                val updatedBitmap = drawBoundingBoxesOnFaces(bitmap, detectedFaces, faceNames)
                faces = detectedFaces
                bitmapWithFaces = updatedBitmap
            }
        }
    }

    Column {
        // Display the image with bounding boxes and handle clicks
        Box(
            modifier = Modifier
                .weight(1f)
                .pointerInput(faces) {
                    detectTapGestures { offset ->
                        bitmapWithFaces?.let { bitmap ->

                            // Calculate the scale factors between the bitmap and the Box
                            val scaleX = this.size.width / bitmap.width.toFloat()
                            val scaleY = this.size.height / bitmap.height.toFloat()

                            selectedFace = faces.firstOrNull { face ->
                                val mappedBoundingBox = androidx.compose.ui.geometry.Rect(
                                    left = face.boundingBox.left * scaleX,
                                    top = face.boundingBox.top * scaleY,
                                    right = face.boundingBox.right * scaleX,
                                    bottom = face.boundingBox.bottom * scaleY
                                )

                                // Convert the tap offset to an Offset and check if it's inside the bounding box
                                mappedBoundingBox.contains(Offset(offset.x, offset.y))
                            }

                            selectedFace?.let {
                                // Update enteredName with the existing name if available
                                enteredName = faceNames[it.trackingId ?: it.hashCode()].orEmpty()
                                showDialog = true
                            }
                        }
                    }
                }
        ) {
            bitmapWithFaces?.asImageBitmap()?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Floating Action Button for saving the image
        FloatingActionButton(
            onClick = { bitmapWithFaces?.let { onSaveImage(it) } },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Filled.FileDownload,
                contentDescription = context.getString(R.string.image_content_description)
            )
        }
    }

    // Show dialog when a face is clicked
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.assign_name)) },
            text = {
                Column {
                    Text(stringResource(R.string.enter_name_for_the_selected_face))
                    OutlinedTextField(
                        value = enteredName,
                        onValueChange = { enteredName = it },
                        label = { Text(stringResource(R.string.name)) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedFace?.let { face ->
                            faceNames = faceNames.toMutableMap().apply {
                                put(face.trackingId ?: face.hashCode(), enteredName)
                            }
                            bitmapWithFaces = drawBoundingBoxesOnFaces(bitmap, faces, faceNames)
                        }
                        showDialog = false
                        enteredName = ""
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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

// In drawBoundingBoxesOnFaces, ensure only modifications are made.
fun drawBoundingBoxesOnFaces(
    bitmap: Bitmap?,
    faces: List<Face>,
    faceNames: Map<Int, String>
): Bitmap? {
    if (bitmap == null) return null

    val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(outputBitmap)

    val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
    }

    for (face in faces) {
        val bounds: Rect = face.boundingBox
        canvas.drawRect(bounds, paint)

        val boxHeight = bounds.height()
        textPaint.textSize = boxHeight * 0.15f

        val name = faceNames[face.trackingId ?: face.hashCode()]

        if (!name.isNullOrEmpty()) {
            canvas.drawText(name, bounds.left.toFloat(), bounds.top.toFloat() - 10, textPaint)
        }
    }

    return outputBitmap
}

@Preview
@Composable
fun PreviewFaceDetectionUI() {
    FaceDetectionUI(null) { _ -> }
}