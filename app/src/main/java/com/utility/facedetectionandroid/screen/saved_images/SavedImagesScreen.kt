package com.utility.facedetectionandroid.screen.saved_images

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.utility.facedetectionandroid.R
import com.utility.facedetectionandroid.utils.ImageUtil

@Composable
fun SavedImagesScreen() {
    val context = LocalContext.current
    val savedImages = remember { ImageUtil.getSavedImagesFromStorage(context) }

    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(savedImages) { file ->
            val painter = rememberAsyncImagePainter(
                model = file,
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(300.dp)
                    .clickable {
                        selectedImage = ImageUtil.loadBitmapFromFile(file)
                        isDialogOpen = true
                    }
            )
        }
    }

    if (isDialogOpen && selectedImage != null) {
        Dialog(onDismissRequest = { isDialogOpen = false }) {
            Surface(
                modifier = Modifier
                    .background(Color.Black),
                color = Color.Black
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        bitmap = selectedImage!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    Button(
                        onClick = { isDialogOpen = false },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}
