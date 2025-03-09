package com.zekri_ahmed.ip_tv_player.domain.repository

import androidx.media3.exoplayer.ExoPlayer

interface MediaController {
    fun play(mediaUrl: String, title: String = "")
    fun pause()
    fun resume()
    fun seekTo(position: Long)
    fun getPlayer(): ExoPlayer?

}