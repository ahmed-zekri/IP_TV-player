package com.zekri_ahmed.ip_tv_player.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.zekri_ahmed.ip_tv_player.data.local.CacheImpl
import com.zekri_ahmed.ip_tv_player.data.repository.MediaControllerImpl
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {

    @OptIn(UnstableApi::class)
    @Binds
    @Singleton
    abstract fun bindMediaController(
        mediaControllerImpl: MediaControllerImpl
    ): MediaController

    companion object {
        @OptIn(UnstableApi::class)
        @Provides
        @Singleton
        fun provideExoPlayer(@ApplicationContext context: Context, cache: Cache): ExoPlayer {
            val dataSourceFactory = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(
                    DefaultDataSource.Factory(context)
                )

            // Create media source factory
            val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

            // Create player
            return ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()
        }

        @Provides
        @Singleton
        @UnstableApi
        fun provideCache(@ApplicationContext context: Context): Cache = CacheImpl(context)

    }
}
