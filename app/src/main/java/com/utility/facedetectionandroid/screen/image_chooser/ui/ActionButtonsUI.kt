package com.utility.facedetectionandroid.screen.image_chooser.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.utility.facedetectionandroid.R
import com.utility.facedetectionandroid.utils.Constants

@Composable
fun ActionButtonsUI(
    buttonText: String,
    onImagePick: (Uri?) -> Unit,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = onImagePick
        )

        // Button to pick an image
        Button(
            modifier = Modifier.height(36.dp),
            onClick = { imagePickerLauncher.launch(Constants.IntentPaths.IMAGE_PICKER_PATH) }
        ) {
            Text(buttonText)
        }

        // Button to view saved images
        Button(
            modifier = Modifier.height(36.dp),
            onClick = { navController.navigate(Constants.Navigation.SAVED_IMAGES) }
        ) {
            Text(text = stringResource(R.string.view_saved_images))
        }
    }
}

@Preview
@Composable
fun PreviewActionButtons() {
    val context = LocalContext.current

    // Mock NavController
    val mockNavController = rememberNavController()

    // Mock lambdas
    val mockOnImagePick: (Uri?) -> Unit = {}

    // Sample button text
    val sampleButtonText = context.getString(R.string.select_image)

    // Call the composable with sample data
    ActionButtonsUI(
        buttonText = sampleButtonText,
        onImagePick = mockOnImagePick,
        navController = mockNavController
    )
}