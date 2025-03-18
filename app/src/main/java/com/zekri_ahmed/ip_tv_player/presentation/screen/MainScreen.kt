package com.zekri_ahmed.ip_tv_player.presentation.screen

import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.zekri_ahmed.ip_tv_player.presentation.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Collect state from the ViewModel
    val playlist by viewModel.playlist.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val currentChannelIndex by viewModel.currentChannelIndex.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalActivity.current
    var isPaused by remember { mutableStateOf(false) }

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

    // Handle full-screen toggle
    val context = LocalContext.current
    LaunchedEffect(playerState.isFullScreen) {
        val activity = context as ComponentActivity
        activity.requestedOrientation = if (playerState.isFullScreen) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (playlist.isNotEmpty())
            VideoPlayerSurface(
                playerState,
                playlist,
                viewModel::resume,
                viewModel::pause,
                viewModel::nextChannel,
                viewModel::previousChannel,
                viewModel::toggleFullScreen,
                isLandscape,
                isPaused
                // Pass the orientation state
            )
        if (!isPaused)
        // Show other UI elements only if not in full-screen mode
            if (!playerState.isFullScreen) {
                if (playlist.isNotEmpty()) {
                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onSearch = { focusManager.clearFocus() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                // Channel List
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredPlaylist) { entry ->
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
                    EmptyPlaylist(onOpenClick = { filePickerLauncher.launch("*/*") })
                }
            }
    }
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onPause(owner: LifecycleOwner) {
                    super.onPause(owner)
                    isPaused = true
                    activity?.enterPictureInPictureMode(
                        PictureInPictureParams.Builder()
                            .setAspectRatio(Rational(16, 9))
                            .build()
                    )
                }

                override fun onResume(owner: LifecycleOwner) {
                    super.onResume(owner)
                    isPaused = false
                }
            }
        )
    }
}