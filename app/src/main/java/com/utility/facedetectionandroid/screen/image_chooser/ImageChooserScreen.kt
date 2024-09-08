package com.utility.facedetectionandroid.screen.image_chooser

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.utility.facedetectionandroid.R
import com.utility.facedetectionandroid.screen.image_chooser.ui.ActionButtonsUI
import com.utility.facedetectionandroid.screen.image_chooser.ui.FaceDetectionUI


@Composable
fun ImageChooserScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onImagePick: (Uri?) -> Unit,
    onSaveImage: (Bitmap) -> Unit,
    selectedBitmap: Bitmap?,
    imageUri: Uri?
) {
    val context = LocalContext.current
    var buttonText by remember { mutableStateOf(context.getString(R.string.select_image)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Action Buttons for selecting image and navigating to saved images
        ActionButtonsUI(
            buttonText = if (imageUri == null) {
                context.getString(R.string.select_image)
            } else {
                context.getString(R.string.select_another_image)
            },
            onImagePick = { uri ->
                buttonText = context.getString(R.string.select_another_image)
                onImagePick(uri)
            },
            navController = navController
        )

        Spacer(modifier = Modifier.weight(1f))

        // Display and process the bitmap
        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedBitmap != null) {
                FaceDetectionUI(bitmap = selectedBitmap) { image ->
                    onSaveImage(image)
                }
            } else {
                Text(
                    text = stringResource(R.string.please_click_on_select_image_to_proceed),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewImageChooserScreen() {
    // Mock Bitmap
    val mockBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    // Mock Uri
    val mockUri = Uri.parse("content://com.example.mockprovider/mockimage.jpg")

    // Mock lambdas
    val mockOnImagePick: (Uri?) -> Unit = {}
    val mockOnSaveImage: (Bitmap) -> Unit = {}

    // Call the composable with sample data
    ImageChooserScreen(
        navController = rememberNavController(),
        onImagePick = mockOnImagePick,
        onSaveImage = mockOnSaveImage,
        selectedBitmap = mockBitmap,
        imageUri = mockUri // Mock URI passed here
    )
}
