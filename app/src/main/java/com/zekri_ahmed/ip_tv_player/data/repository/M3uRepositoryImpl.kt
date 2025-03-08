package com.zekri_ahmed.ip_tv_player.data.repository

import android.net.Uri
import com.zekri_ahmed.ip_tv_player.data.local.M3uLocalDataSource
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.domain.repository.M3uRepository

class M3uRepositoryImpl(private val localDataSource: M3uLocalDataSource) : M3uRepository {
    override suspend fun loadPlaylist(filePath: String): List<M3uEntry> {
        return localDataSource.loadPlaylist(Uri.parse(filePath))
    }
}