package com.video.downloader.core

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.video.downloader.presentation.navigation.AppNavigationGraph
import com.video.downloader.presentation.theme.VideoDownloaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            VideoDownloaderTheme {
                AppNavigationGraph()
            }
        }
    }
}