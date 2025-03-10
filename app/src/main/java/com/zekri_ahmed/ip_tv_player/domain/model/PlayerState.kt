package com.zekri_ahmed.ip_tv_player.domain.model

import androidx.media3.common.Player

data class PlayerState(
    val player: Player? = null, // Expose the player here
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