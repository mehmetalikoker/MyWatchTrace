package com.example.mywatchtrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mywatchtrace.ui.MainScreen
import com.example.mywatchtrace.ui.WatchViewModel
import com.example.mywatchtrace.ui.screens.SplashScreen
import com.example.mywatchtrace.ui.theme.MyWatchTraceTheme

class MainActivity : ComponentActivity() {

    private val watchViewModel: WatchViewModel by viewModels {
        WatchViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyWatchTraceTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(
                        onTimeout = { showSplash = false }
                    )
                } else {
                    MainScreen(viewModel = watchViewModel)
                }
            }
        }
    }
}
