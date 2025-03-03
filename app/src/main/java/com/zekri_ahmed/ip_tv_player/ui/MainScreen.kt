// app/src/main/java/com/example/m3uplayer/ui/MainScreen.kt

@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.zekri_ahmed.ip_tv_player.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.zekri_ahmed.ip_tv_player.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.model.M3uParser
import com.zekri_ahmed.ip_tv_player.service.MediaPlayerService
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@UnstableApi
class MainViewModel : ViewModel() {
    private val _playlist = mutableStateListOf<M3uEntry>()
    val playlist: List<M3uEntry> = _playlist

    private var _serviceFlow: Flow<MediaPlayerService.PlayerState>? = null
    val playerState: Flow<MediaPlayerService.PlayerState>?
        get() = _serviceFlow

    private val _currentFile = mutableStateOf<Uri?>(null)

    // Track the currently playing channel index
    private val _currentChannelIndex = mutableIntStateOf(-1)
    val currentChannelIndex: State<Int> = _currentChannelIndex

    fun loadPlaylist(context: Context, uri: Uri) {
        _currentFile.value = uri

        try {
            // Copy the file to app's storage to ensure we can access it later
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = getFileName(context, uri)

            val file = File(context.filesDir, fileName)

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            // Parse the M3U file
            val parser = M3uParser()
            val entries = parser.parseFile(file)

            _playlist.clear()
            _playlist.addAll(entries)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name ?: "playlist.m3u"
    }


    fun setServiceFlow(flow: Flow<MediaPlayerService.PlayerState>) {
        _serviceFlow = flow
    }

    fun playChannel(index: Int) {
        if (index >= 0 && index < playlist.size) {
            _currentChannelIndex.intValue = index
        }
    }

    fun nextChannel() {
        if (playlist.isNotEmpty()) {
            val newIndex =
                if (_currentChannelIndex.intValue >= playlist.size - 1) 0 else _currentChannelIndex.intValue + 1
            _currentChannelIndex.intValue = newIndex
        }
    }

    fun previousChannel() {
        if (playlist.isNotEmpty()) {
            val newIndex =
                if (_currentChannelIndex.intValue <= 0) playlist.size - 1 else _currentChannelIndex.intValue - 1
            _currentChannelIndex.intValue = newIndex
        }
    }

    fun getCurrentChannel(): M3uEntry? {
        return if (_currentChannelIndex.intValue >= 0 && _currentChannelIndex.intValue < playlist.size) {
            playlist[_currentChannelIndex.intValue]
        } else null
    }
}


@OptIn(UnstableApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    // Service connection
    var mediaService: MediaPlayerService? by remember { mutableStateOf(null) }
    val serviceIntent = remember { Intent(context, MediaPlayerService::class.java) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.loadPlaylist(context, it)
        }
    }

    // Service connection
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MediaPlayerService.MediaPlayerBinder
                mediaService = binder.getService()
                viewModel.setServiceFlow(binder.getService().playerState)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mediaService = null
            }
        }
    }

    // Start and bind to the service
    LaunchedEffect(Unit) {
        context.startForegroundService(serviceIntent)
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    // Cleanup when leaving the composition
    DisposableEffect(Unit) {
        onDispose {
            context.unbindService(serviceConnection)
        }
    }

    // Player state
    val playerState by produceState(
        initialValue = MediaPlayerService.PlayerState()
    ) {
        viewModel.playerState?.collect {
            value = it
        }
    }

    // Observe current channel changes to start playback
    val currentChannelIndex by viewModel.currentChannelIndex
    LaunchedEffect(currentChannelIndex) {
        viewModel.getCurrentChannel()?.let { channel ->
            mediaService?.play(channel.path, channel.title)
        }
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("M3U Player with TimeShift") },
                actions = {
                    IconButton(onClick = { filePickerLauncher.launch("*/*") }) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Open Playlist"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Video player surface using PlayerView
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = mediaService?.player // Ensure mediaService is not null
                        useController = true // Enable playback controls
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT // Adjust video scaling
                    }
                },
                update = { playerView ->
                    playerView.player = mediaService?.player // Ensure player is always set on recomposition
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f) // Adjust aspect ratio as needed
            )

            // Player controls
            PlayerControls(
                playerState = playerState,
                onPlay = {
                    mediaService?.resume()
                },
                onPause = {
                    mediaService?.pause()
                },
                onSeek = { position ->
                    mediaService?.seekTo(position)
                },
                onPreviousChannel = {
                    viewModel.previousChannel()
                },
                onNextChannel = {
                    viewModel.nextChannel()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Channel indicators
            if (viewModel.playlist.isNotEmpty()) {
                ChannelIndicator(
                    currentIndex = currentChannelIndex,
                    totalChannels = viewModel.playlist.size,
                    currentTitle = viewModel.getCurrentChannel()?.title ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Channel list
            Text(
                text = "Channels",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(viewModel.playlist.withIndex().toList()) { (index, entry) ->
                    PlaylistItem(
                        entry = entry,
                        isPlaying = index == currentChannelIndex,
                        onClick = {
                            viewModel.playChannel(index)
                        }
                    )
                }

                if (viewModel.playlist.isEmpty()) {
                    item {
                        EmptyPlaylist(
                            onOpenClick = { filePickerLauncher.launch("*/*") }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun PlayerControls(
    playerState: MediaPlayerService.PlayerState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onPreviousChannel: () -> Unit,
    onNextChannel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Title
        Text(
            text = playerState.title.ifEmpty { "No media playing" },
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(formatDuration(playerState.currentPosition))

            Slider(
                value = if (playerState.duration > 0) {
                    playerState.currentPosition.toFloat() / playerState.duration
                } else 0f,
                onValueChange = { value ->
                    val newPosition = (value * playerState.duration).toLong()
                    onSeek(newPosition)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            Text(formatDuration(playerState.duration))
        }

        // Buffer progress
        LinearProgressIndicator(
            progress = {
                if (playerState.duration > 0) {
                    playerState.bufferedPosition.toFloat() / playerState.duration
                } else 0f
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Playback controls
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Channel controls
            IconButton(onClick = onPreviousChannel) {
                Icon(Icons.Default.SkipPrevious, "Previous Channel")
            }

            // Timeshift controls
            IconButton(onClick = { onSeek(playerState.currentPosition - 10000) }) {
                Icon(Icons.Default.Replay10, "Rewind 10s")
            }

            IconButton(
                onClick = {
                    if (playerState.isPlaying) onPause() else onPlay()
                }
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = if (playerState.isPlaying) "Pause" else "Play"
                )
            }

            IconButton(onClick = { onSeek(playerState.currentPosition + 30000) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Forward 30s")
            }

            // Next channel
            IconButton(onClick = onNextChannel) {
                Icon(Icons.Default.SkipNext, "Next Channel")
            }
        }
    }
}

@Composable
fun ChannelIndicator(
    currentIndex: Int,
    totalChannels: Int,
    currentTitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Current Channel",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = currentTitle.ifEmpty { "Channel ${currentIndex + 1}" },
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Channel ${currentIndex + 1} of $totalChannels",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun PlaylistItem(
    entry: M3uEntry,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(entry.title.ifEmpty { entry.path.split("/").last() })
        },
        supportingContent = {
            if (entry.duration > 0) {
                Text("Duration: ${formatDuration(entry.duration)}")
            }
        },
        leadingContent = {
            Icon(
                imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Tv,
                contentDescription = null
            )
        },
        trailingContent = {
            if (isPlaying) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Currently Playing",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
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

// Helper function to format duration
@SuppressLint("DefaultLocale")
fun formatDuration(durationMs: Long): String {
    if (durationMs <= 0) return "0:00"

    val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}