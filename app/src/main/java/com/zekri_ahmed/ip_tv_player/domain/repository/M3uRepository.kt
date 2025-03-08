package com.zekri_ahmed.ip_tv_player.domain.repository

import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry

interface M3uRepository {
    suspend fun loadPlaylist(filePath: String): List<M3uEntry>
}