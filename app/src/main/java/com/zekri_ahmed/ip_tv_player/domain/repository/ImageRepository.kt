package com.zekri_ahmed.ip_tv_player.domain.repository
import android.graphics.Bitmap

interface ImageRepository {
    suspend fun loadBitmapFromUrl(url: String): Bitmap?
}