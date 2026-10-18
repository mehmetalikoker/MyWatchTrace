package com.example.mywatchtrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.mywatchtrace.ui.MainScreen
import com.example.mywatchtrace.ui.WatchViewModel
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
                MainScreen(viewModel = watchViewModel)
            }
        }
    }
}
