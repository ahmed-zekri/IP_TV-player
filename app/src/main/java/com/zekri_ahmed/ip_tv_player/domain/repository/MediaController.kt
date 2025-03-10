package com.zekri_ahmed.ip_tv_player.domain.repository

import com.zekri_ahmed.ip_tv_player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow


interface MediaController {
    fun play(mediaUrl: String, title: String = "")
    fun pause()
    fun resume()
    fun seekTo(position: Long)
    fun getState(): StateFlow<PlayerState>
}