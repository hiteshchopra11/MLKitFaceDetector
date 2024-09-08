package com.utility.facedetectionandroid.screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utility.facedetectionandroid.MainViewModel
import com.utility.facedetectionandroid.R
import com.utility.facedetectionandroid.screen.image_chooser.ImageChooserScreen
import com.utility.facedetectionandroid.screen.saved_images.SavedImagesScreen
import com.utility.facedetectionandroid.utils.Constants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current

    val onImagePick: (Uri?) -> Unit = { uri ->
        viewModel.setImageUri(context, uri)
    }

    val onSaveImage: (Bitmap) -> Unit = { bitmap ->
        viewModel.saveImageToStorage(context, bitmap)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.face_detection_app)) },
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Constants.Navigation.IMAGE_CHOOSER
        ) {
            composable(Constants.Navigation.IMAGE_CHOOSER) {
                ImageChooserScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    onImagePick = onImagePick,
                    onSaveImage = onSaveImage,
                    selectedBitmap = viewModel.selectedBitmap,
                    imageUri = viewModel.imageUri
                )
            }

            composable(Constants.Navigation.SAVED_IMAGES) {
                SavedImagesScreen()
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainScreenContentUI() {
    MainScreenContent(
        navController = rememberNavController(),
        viewModel = viewModel()
    )
}