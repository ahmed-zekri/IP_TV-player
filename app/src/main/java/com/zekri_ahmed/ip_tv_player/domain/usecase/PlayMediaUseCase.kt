package com.zekri_ahmed.ip_tv_player.domain.usecase

import com.zekri_ahmed.ip_tv_player.data.repository.PlayerState
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PlayMediaUseCase @Inject constructor(
    private val mediaController: MediaController
) {
    private val _playerState =
        MutableStateFlow(PlayerState(player = mediaController.getPlayer(), isPlaying = false))
    val playerState: StateFlow<PlayerState> = _playerState
    operator fun invoke(channel: M3uEntry) {
        mediaController.play(channel.path, channel.title)
    }

    fun pause() {
        mediaController.pause()
        _playerState.update { it.copy(isPlaying = false) }

    }

    fun resume() {
        mediaController.resume()
        _playerState.update { it.copy(isPlaying = true) }
    }

    fun seekTo(position: Long) {
        mediaController.seekTo(position)
        _playerState.update { it.copy(currentPosition = position) }
    }
}