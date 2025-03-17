package com.zekri_ahmed.ip_tv_player.presentation.state

import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry

data class PlayerState(
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val currentPosition: Long = 0,
    val isFullScreen: Boolean = false,
    val m3uEntry: M3uEntry? = null,
    val player: Any? = null,
    val isLoading: Boolean = false,
    val playerError: String? = null,
    val currentMediaUrl: String? = null
)