package com.zekri_ahmed.ip_tv_player.presentation.screen

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry

@Composable
@Preview
fun VideoPlayerSurface(
    playerState: PlayerState = PlayerState(),
    playlist: List<M3uEntry> = listOf(),
    resume: () -> Unit = {},
    pause: () -> Unit = {},
    nextChannel: () -> Unit = {},
    previousChannel: () -> Unit = {},
    toggleFullScreen: () -> Unit = {}
) {


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .run {
                if (playerState.isFullScreen) this else aspectRatio(16f / 9f)
            }

    ) {
        // Use AndroidView to embed the PlayerView in our Compose UI
        Box {
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
                    if (playerState.player != null)
                        playerView.player = playerState.player as Player
                }
            )

            if (playerState.isLoading && !playerState.isPlaying)
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            if (playerState.playerError != null) {
                Column(Modifier.align(Alignment.Center)) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = playerState.playerError,
                        tint = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(playerState.playerError, color = Color.White)
                }
            }

        }

        // Pullable controls overlay
        if (playlist.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
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