package com.example.mywatchtrace.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.mywatchtrace.R
import kotlinx.coroutines.delay
import java.io.File

val SPLASH_IMAGES = listOf(
    R.drawable.img,
    R.drawable.img2
)

@Composable
fun SplashScreen(
    splashImageUri: String,
    onTimeout: () -> Unit
) {
    val randomImageRes = remember {
        try {
            SPLASH_IMAGES.random()
        } catch (_: Exception) {
            R.drawable.img
        }
    }

    LaunchedEffect(Unit) {
        delay(2000) // 2 saniye bekle
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (splashImageUri.isNotBlank()) {
            val model = remember(splashImageUri) {
                if (splashImageUri.startsWith("/")) File(splashImageUri) else splashImageUri
            }
            AsyncImage(
                model = model,
                contentDescription = "Özel Açılış Görseli",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = randomImageRes),
                contentDescription = "Açılış Görseli",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
