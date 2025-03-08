package com.zekri_ahmed.ip_tv_player.data.local

import android.content.Context
import android.net.Uri
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry

class M3uLocalDataSource(private val context: Context) {

    private val m3uParser = M3uParser() // Initialize M3uParser

    // M3uLocalDataSource.kt - update the loadPlaylist method
    fun loadPlaylist(uri: Uri): List<M3uEntry> {
        val contentResolver = context.contentResolver
        return contentResolver.openInputStream(uri)?.use { inputStream ->
            m3uParser.parse(inputStream)
        } ?: emptyList()
    }


}