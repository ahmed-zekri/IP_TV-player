package com.zekri_ahmed.ip_tv_player.domain.usecase

import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.domain.repository.M3uRepository

class LoadPlaylistUseCase(private val repository: M3uRepository) {
    suspend operator fun invoke(filePath: String): List<M3uEntry> {
        return repository.loadPlaylist(filePath)
    }
}