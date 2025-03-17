package com.zekri_ahmed.ip_tv_player.domain.repository

import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import kotlinx.coroutines.flow.StateFlow


interface MediaController {
    fun play(mediaUrl: String, title: String = "")
    fun pause()
    fun resume()
    fun seekTo(position: Long)
    fun getState(): StateFlow<PlayerState>
}

data class PlayerState(
    val player: Any? = null, // Expose the player here
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val currentPosition: Long = 0,
    val bufferedPosition: Long = 0,
    val m3uEntry: M3uEntry? = null,
    val isFullScreen: Boolean = false,
    val isLoading: Boolean = false,
    val playerError: String? = null
)