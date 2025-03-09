package com.zekri_ahmed.ip_tv_player.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.domain.repository.M3uRepository
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import com.zekri_ahmed.ip_tv_player.domain.usecase.LoadPlaylistUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.PlayMediaUseCase
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
@Preview
fun MainScreenPreview() {
    val viewModel = MainViewModel(
        LoadPlaylistUseCase(object : M3uRepository {
            override suspend fun loadPlaylist(filePath: String): List<M3uEntry> =
                listOf()

        }),
        playMediaUseCase = PlayMediaUseCase(object : MediaController {
            override fun play(mediaUrl: String, title: String) {

            }

            override fun pause() {

            }

            override fun resume() {

            }

            override fun seekTo(position: Long) {

            }
        }),
    )
    MainScreen(viewModel)


}
