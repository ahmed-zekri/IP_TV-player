// Data Layer
package com.zekri_ahmed.ip_tv_player.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.zekri_ahmed.ip_tv_player.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepositoryImpl(
    private val context: Context,
    private val imageLoader: ImageLoader
) : ImageRepository {

    override suspend fun loadBitmapFromUrl(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .build()
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    (result.drawable as BitmapDrawable).bitmap
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}