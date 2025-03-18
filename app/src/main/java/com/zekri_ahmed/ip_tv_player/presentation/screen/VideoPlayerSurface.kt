package com.zekri_ahmed.ip_tv_player.presentation.screen

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.presentation.state.PlayerState
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
@Preview
fun VideoPlayerSurface(
    playerState: PlayerState = PlayerState(
        currentIndex = -1,
        currentM3uEntries = emptyList()
    ),
    playlist: List<M3uEntry> = emptyList(),
    resume: () -> Unit = {},
    pause: () -> Unit = {},
    nextChannel: () -> Unit = {},
    previousChannel: () -> Unit = {},
    toggleFullScreen: () -> Unit = {},
    isLandscape: Boolean = false,
    isPaused: Boolean = false
) {
    var isControlsVisible by remember { mutableStateOf(true) }
    // LaunchedEffect to hide controls after a delay
    LaunchedEffect(isControlsVisible) {
        if (isControlsVisible) {
            delay(3000) // Hide controls after 3 seconds of inactivity
            isControlsVisible = false
        }
    }

    // LaunchedEffect to show controls when the channel changes or player state changes
    LaunchedEffect(playerState.currentMediaUrl, playerState.isPlaying, playerState.isPaused) {
        isControlsVisible = true // Show controls when the channel or player state changes
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .run {
                if (isLandscape) fillMaxSize() else aspectRatio(16f / 9f)
            }
            .clickable {
                isControlsVisible = true // Show controls when the player is clicked
            }
    ) {
        // Use AndroidView to embed the PlayerView in our Compose UI
        Box(modifier = if (isPaused) Modifier.fillMaxSize() else Modifier) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                        useController = false
                        keepScreenOn = true
                    }
                },
                update = { playerView ->
                    if (playerState.player != null)
                        playerView.player = playerState.player as Player
                }
            )

            if (playerState.isLoading && !playerState.isPlaying && !playerState.isPaused)
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )

            if (playerState.playerError != null) {
                Column(Modifier.align(Alignment.Center)) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = playerState.playerError,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(playerState.playerError, color = Color.White)
                }
            }
        }

        // Pullable controls overlay
        if (!isPaused)
            if (playlist.isNotEmpty()) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    AnimatedVisibility(
                        visible = isControlsVisible,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        PlayerControls(
                            isPlaying = playerState.isPlaying,
                            onPlay = resume,
                            onPause = pause,
                            onNext = nextChannel,
                            onPrevious = previousChannel,
                            onToggleFullScreen = toggleFullScreen
                        )
                    }
                }
            }

    }
}