package com.utility.facedetectionandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.utility.facedetectionandroid.screen.MainScreenContent
import com.utility.facedetectionandroid.ui.theme.FaceDetectionAndroidTheme

class MainActivity : ComponentActivity() {

    // Initialize the ViewModel
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FaceDetectionAndroidTheme {
                val navController = rememberNavController()
                MainScreenContent(navController = navController, viewModel = mainViewModel)
            }
        }
    }
}