package com.utility.facedetectionandroid

import android.graphics.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
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
    var selectedFace by remember { mutableStateOf<Face?>(null) }
    val scope = rememberCoroutineScope()

    // Map to store names for each face
    var faceNames by remember { mutableStateOf(mapOf<Int, String>()) }
    var showDialog by remember { mutableStateOf(false) }
    var enteredName by remember { mutableStateOf("") }

    // Start face detection and recompose only when bitmap instance changes
    LaunchedEffect(key1 = bitmap) {
        scope.launch(Dispatchers.IO) {
            faces = detectFaces(bitmap = bitmap)
            bitmapWithFaces = drawBoundingBoxesOnFaces(
                bitmap = bitmap,
                faces = faces,
                faceNames = faceNames
            )
        }
    }

    // Display the image with bounding boxes and handle clicks
    Box(
        modifier = Modifier
            .fillMaxSize()
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


    // Show dialog when a face is clicked
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Assign Name") },
            text = {
                Column {
                    Text("Enter name for the selected face:")
                    OutlinedTextField(
                        value = enteredName,
                        onValueChange = { enteredName = it },
                        label = { Text("Name") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Assign name to the face
                        selectedFace?.let { face ->
                            // Safely create a new map and update the value
                            faceNames = faceNames.toMutableMap().apply {
                                put(face.trackingId ?: face.hashCode(), enteredName)
                            }
                        }

                        // Redraw bitmap with updated names
                        bitmapWithFaces = drawBoundingBoxesOnFaces(
                            bitmap = bitmap,
                            faces = faces,
                            faceNames = faceNames
                        )

                        showDialog = false
                        enteredName = ""
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
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

// Draw bounding boxes and face names on the detected faces
fun drawBoundingBoxesOnFaces(
    bitmap: Bitmap?,
    faces: List<Face>,
    faceNames: Map<Int, String>
): Bitmap? {
    val outputBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = outputBitmap?.let { Canvas(it) }

    val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    val textPaint = Paint().apply {
        color = Color.WHITE
        // Initial text size, can be adjusted later
        textSize = 50f
    }

    for (face in faces) {
        val bounds: Rect = face.boundingBox
        canvas?.drawRect(bounds, paint)

        // Dynamically adjust text size based on the bounding box height
        val boxHeight = bounds.height()
        textPaint.textSize = boxHeight * 0.15f // Set text size proportional to box height (15% of height)

        // Draw name above the bounding box if available
        val name = faceNames[face.trackingId ?: face.hashCode()]
        if (!name.isNullOrEmpty()) {
            canvas?.drawText(name, bounds.left.toFloat(), bounds.top.toFloat() - 10, textPaint)
        }
    }

    return outputBitmap
}