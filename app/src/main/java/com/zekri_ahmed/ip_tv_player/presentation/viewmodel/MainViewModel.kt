package com.zekri_ahmed.ip_tv_player.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.domain.usecase.LoadPlaylistUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.PlayMediaUseCase
import com.zekri_ahmed.ip_tv_player.presentation.screen.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loadPlaylistUseCase: LoadPlaylistUseCase,
    private val playMediaUseCase: PlayMediaUseCase
) : ViewModel() {

    // State for the playlist
    private val _playlist = MutableStateFlow<List<M3uEntry>>(emptyList())
    val playlist: StateFlow<List<M3uEntry>> = _playlist

    // State for the currently playing channel index
    private val _currentChannelIndex = MutableStateFlow(-1)
    val currentChannelIndex: StateFlow<Int> = _currentChannelIndex

    // State for fullscreen mode
    private val _isFullScreen = MutableStateFlow(false)
    val isFullScreen: StateFlow<Boolean> = _isFullScreen

    // State for player state
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState

    init {
        // The key part: collect changes from the original media player state
        viewModelScope.launch {
            // This assumes playMediaUseCase.playerState is a Flow that emits updates
            // whenever the underlying media player state changes
            playMediaUseCase.playerState.collect { mediaPlayerState ->
                _playerState.value = PlayerState(
                    isPlaying = mediaPlayerState.isPlaying,
                    currentPosition = mediaPlayerState.currentPosition,
                    duration = mediaPlayerState.duration,
                    player = mediaPlayerState.player
                )
            }
        }
    }

    // Load the playlist
    fun loadPlaylist(filePath: String) {
        viewModelScope.launch {
            _playlist.value = loadPlaylistUseCase(filePath)

            // Automatically play first channel if playlist is not empty
            if (_playlist.value.isNotEmpty()) {
                playChannel(0)
            }
        }
    }

    // Play a specific channel
    fun playChannel(index: Int) {
        if (index >= 0 && index < _playlist.value.size) {
            _currentChannelIndex.value = index
            val currentChannel = _playlist.value[index]
            playMediaUseCase(currentChannel)
        }
    }

    // Play the next channel
    fun nextChannel() {
        if (_playlist.value.isNotEmpty()) {
            val newIndex = if (_currentChannelIndex.value >= _playlist.value.size - 1) {
                0 // Wrap around to the first channel
            } else {
                _currentChannelIndex.value + 1
            }
            playChannel(newIndex)
        }
    }

    // Play the previous channel
    fun previousChannel() {
        if (_playlist.value.isNotEmpty()) {
            val newIndex = if (_currentChannelIndex.value <= 0) {
                _playlist.value.size - 1 // Wrap around to the last channel
            } else {
                _currentChannelIndex.value - 1
            }
            playChannel(newIndex)
        }
    }

    // Get the currently playing channel
    fun getCurrentChannel(): M3uEntry? {
        return if (_currentChannelIndex.value >= 0 && _currentChannelIndex.value < _playlist.value.size) {
            _playlist.value[_currentChannelIndex.value]
        } else {
            null
        }
    }

    // Toggle fullscreen mode
    fun toggleFullScreen() {
        _isFullScreen.value = !_isFullScreen.value
        _playerState.value = playerState.value.copy(isFullScreen = !_isFullScreen.value)
    }

    // Player control methods
    fun pause() {
        playMediaUseCase.pause()
    }

    fun resume() {
        playMediaUseCase.resume()
    }

    fun seekTo(position: Long) {
        playMediaUseCase.seekTo(position)
    }

}