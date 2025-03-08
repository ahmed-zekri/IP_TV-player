package com.zekri_ahmed.ip_tv_player.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.presentation.viewmodel.MainViewModel

@Composable
@Preview
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    // Collect state from the ViewModel
    val playlist by viewModel.playlist.collectAsState()
    val currentChannelIndex by viewModel.currentChannelIndex.collectAsState()
    // File picker launcher

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.path?.let {
            viewModel.loadPlaylist(it)
        }
    }
    // Track play/pause state
    var isPlaying by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Video Player Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Video player placeholder
            Text(
                text = "Video Player",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Channel List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(playlist) { entry ->
                ChannelItem(
                    entry = entry,
                    isPlaying = playlist.indexOf(entry) == currentChannelIndex,
                    onClick = {
                        viewModel.playChannel(playlist.indexOf(entry))
                        isPlaying = true
                    }
                )
            }
        }
        if (playlist.isEmpty()) {

            EmptyPlaylist(
                onOpenClick = { filePickerLauncher.launch("*/*") }
            )


            // Player Controls
            PlayerControls(
                isPlaying = isPlaying,
                onPlay = {
                    viewModel.resume()
                    isPlaying = true
                },
                onPause = {
                    viewModel.pause()
                    isPlaying = false
                },
                onNext = {
                    viewModel.nextChannel()
                    isPlaying = true
                },
                onPrevious = {
                    viewModel.previousChannel()
                    isPlaying = true
                },
                onToggleFullScreen = { viewModel.toggleFullScreen() }
            )
        }
    }


}

@Composable
fun ChannelItem(
    entry: M3uEntry,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium
            )
            if (isPlaying) {
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleFullScreen: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
        }

        // Conditional play/pause button
        if (isPlaying) {
            IconButton(onClick = onPause) {
                Icon(Icons.Default.Pause, contentDescription = "Pause")
            }
        } else {
            IconButton(onClick = onPlay) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }
        }

        IconButton(onClick = onNext) {
            Icon(Icons.Default.SkipNext, contentDescription = "Next")
        }
        IconButton(onClick = onToggleFullScreen) {
            Icon(Icons.Default.Fullscreen, contentDescription = "Toggle Fullscreen")
        }
    }
}

@Composable
fun EmptyPlaylist(
    onOpenClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("No playlist loaded")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onOpenClick) {
                Text("Open M3U Playlist")
            }
        }
    }
}



