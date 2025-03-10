package com.zekri_ahmed.ip_tv_player.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
//show dark background in preview to see white icons better
@Preview(showBackground = true, backgroundColor = 0xFF333333)
fun PlayerControls(
    isPlaying: Boolean = false,
    onPlay: () -> Unit = {},
    onPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onToggleFullScreen: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPrevious,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .size(48.dp)
        ) {
            Icon(
                Icons.Default.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White
            )
        }

        // Conditional play/pause button
        if (isPlaying) {
            IconButton(
                onClick = onPause,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .size(48.dp)
            ) {
                Icon(
                    Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White
                )
            }
        } else {
            IconButton(
                onClick = onPlay,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .size(48.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
        }

        IconButton(
            onClick = onNext,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .size(48.dp)
        ) {
            Icon(
                Icons.Default.SkipNext,
                contentDescription = "Next",
                tint = Color.White
            )
        }

        IconButton(
            onClick = onToggleFullScreen,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .size(48.dp)
        ) {
            Icon(
                Icons.Default.Fullscreen,
                contentDescription = "Toggle Fullscreen",
                tint = Color.White
            )
        }
    }
}