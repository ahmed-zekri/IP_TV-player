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
                isPlaying = viewModel.isPlaying(),
                onPlay = {
                    viewModel.resume()
                },
                onPause = {
                    viewModel.pause()
                },
                onNext = {
                    viewModel.nextChannel()
                },
                onPrevious = {
                    viewModel.previousChannel()
                },
                onToggleFullScreen = { viewModel.toggleFullScreen() }
            )
        }
    }


}
