package com.zekri_ahmed.ip_tv_player.presentation.screen

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.zekri_ahmed.ip_tv_player.presentation.viewmodel.MainViewModel

@Composable
fun VideoPlayerSurface(viewModel: MainViewModel = hiltViewModel()) {
    val isFullScreen by viewModel.isFullScreen.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .run {
                if (isFullScreen) this else aspectRatio(16f / 9f)
            }
    ) {
        // Use AndroidView to embed the PlayerView in our Compose UI
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                    keepScreenOn = true
                }
            },
            update = { playerView ->
                // Directly get the player from our ViewModel
                playerView.player = viewModel.getPlayer()
            }
        )
    }
}