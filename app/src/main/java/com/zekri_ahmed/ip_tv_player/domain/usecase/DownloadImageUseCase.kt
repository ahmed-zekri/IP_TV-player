package com.zekri_ahmed.ip_tv_player.domain.usecase

import android.graphics.Bitmap
import com.zekri_ahmed.ip_tv_player.domain.repository.ImageRepository


class DownloadImageUseCase(private val repository: ImageRepository) {
        suspend operator fun invoke(url:String): Bitmap? {
            return repository.loadBitmapFromUrl(url)
        }
    }
