package com.zekri_ahmed.ip_tv_player.domain.repository

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
    val currentPosition: Long = 0,
    val bufferedPosition: Long = 0,
    val duration: Long = 0,
    val currentMediaUrl: String = "",
    val title: String = "",
    val isFullScreen: Boolean = false,
    val isLoading: Boolean = false,
    val playerError: String? = null
)