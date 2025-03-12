package com.zekri_ahmed.ip_tv_player.domain.usecase

import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.domain.repository.M3uRepository

class GetLastLoadedPlaylistUseCase(private val repository: M3uRepository) {
     operator fun invoke(): List<M3uEntry>? {
        return repository.getLastLoadedPlaylist()
    }
}