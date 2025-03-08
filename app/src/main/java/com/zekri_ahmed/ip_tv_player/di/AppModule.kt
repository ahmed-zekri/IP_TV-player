package com.zekri_ahmed.ip_tv_player.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.zekri_ahmed.ip_tv_player.data.local.M3uLocalDataSource
import com.zekri_ahmed.ip_tv_player.data.repository.M3uRepositoryImpl
import com.zekri_ahmed.ip_tv_player.domain.repository.M3uRepository
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import com.zekri_ahmed.ip_tv_player.domain.usecase.LoadPlaylistUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.PlayMediaUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideM3uLocalDataSource(@ApplicationContext context: Context): M3uLocalDataSource {
        return M3uLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideM3uRepository(localDataSource: M3uLocalDataSource): M3uRepository {
        return M3uRepositoryImpl(localDataSource)
    }

    @Provides
    @Singleton
    fun provideLoadPlaylistUseCase(repository: M3uRepository): LoadPlaylistUseCase {
        return LoadPlaylistUseCase(repository)
    }
    @Provides
    @Singleton
    fun providePlayMediaUseCase(
        mediaController: MediaController
    ): PlayMediaUseCase {
        return PlayMediaUseCase(mediaController)
    }


    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheDir = File(context.cacheDir, "media_cache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val evictor = LeastRecentlyUsedCacheEvictor(1024 * 1024 * 1024) // 1GB cache
        return SimpleCache(cacheDir, evictor)
    }
}