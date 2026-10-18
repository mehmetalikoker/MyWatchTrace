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
import com.example.mywatchtrace.R
import kotlinx.coroutines.delay

// Buraya ileride yeni ekleyeceğiniz resimleri de ekleyebilirsiniz:
// Örn: R.drawable.img2, R.drawable.img3 vb.
val SPLASH_IMAGES = listOf(
    R.drawable.img
)

@Composable
fun SplashScreen(
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
        Image(
            painter = painterResource(id = randomImageRes),
            contentDescription = "Açılış Görseli",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
