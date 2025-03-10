package com.zekri_ahmed.ip_tv_player.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zekri_ahmed.ip_tv_player.presentation.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    // Collect state from the ViewModel
    val playlist by viewModel.playlist.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val currentChannelIndex by viewModel.currentChannelIndex.collectAsState()

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    // Filtered playlist based on search query
    val filteredPlaylist = if (searchQuery.isBlank()) {
        playlist
    } else {
        playlist.filter { entry ->
            entry.title.contains(searchQuery, ignoreCase = true) || entry.tvgName?.contains(
                searchQuery,
                ignoreCase = true
            ) == true || entry.groupTitle?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.toString()?.let {
            viewModel.loadPlaylist(it)
        }
    }

    // Focus manager to clear focus when the user submits the search
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {

        if (playlist.isNotEmpty()) {
            VideoPlayerSurface(
                playerState,
                playlist,
                viewModel::resume,
                viewModel::pause,
                viewModel::nextChannel,
                viewModel::previousChannel,
                viewModel::toggleFullScreen
            )
        }
        if (playlist.isNotEmpty())
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearch = { focusManager.clearFocus() }, // Clear focus when the user submits the search
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        // Channel List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredPlaylist) { entry ->
                ChannelItem(entry = entry,
                    isPlaying = playlist.indexOf(entry) == currentChannelIndex,
                    onClick = {
                        viewModel.playChannel(playlist.indexOf(entry))
                    })
            }
        }

        if (playlist.isEmpty()) {
            EmptyPlaylist(onOpenClick = { filePickerLauncher.launch("*/*") })
        }
    }
}
