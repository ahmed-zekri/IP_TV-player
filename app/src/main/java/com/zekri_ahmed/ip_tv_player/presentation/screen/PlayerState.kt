package com.zekri_ahmed.ip_tv_player.presentation.screen

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val isFullScreen: Boolean = false,
    val duration: Long = 0,
    val player: Any? = null,
    val isLoading: Boolean = false,
    val playerError: String? = null
)