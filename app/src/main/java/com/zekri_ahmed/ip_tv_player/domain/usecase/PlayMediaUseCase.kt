package com.zekri_ahmed.ip_tv_player.domain.usecase

import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import com.zekri_ahmed.ip_tv_player.domain.repository.PlayerState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PlayMediaUseCase @Inject constructor(
    private val mediaController: MediaController
) {
    private val _playerState = mediaController.getState()
    val playerState: StateFlow<PlayerState> = _playerState
    operator fun invoke(index: Int) {
        mediaController.play(index)
    }

    fun pause() {
        mediaController.pause()

    }

    fun resume() {
        mediaController.resume()
    }

    fun seekTo(position: Long) {
        mediaController.seekTo(position)
    }

}