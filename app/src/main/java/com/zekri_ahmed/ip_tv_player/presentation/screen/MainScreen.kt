package com.zekri_ahmed.ip_tv_player.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.zekri_ahmed.ip_tv_player.presentation.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    // Collect state from the ViewModel
    val playlist by viewModel.playlist.collectAsState()
    val currentChannelIndex by viewModel.currentChannelIndex.collectAsState()
    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.toString()?.let {
            viewModel.loadPlaylist(it)
        }
    }
    // Track play/pause state
    var isPlaying by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        VideoPlayerSurface(viewModel)

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
