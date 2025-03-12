package com.zekri_ahmed.ip_tv_player.di

import android.app.NotificationManager
import android.content.Context
import androidx.media3.session.MediaSession
import coil.ImageLoader
import com.zekri_ahmed.ip_tv_player.data.local.M3uLocalDataSource
import com.zekri_ahmed.ip_tv_player.data.notification.NotificationProviderImpl
import com.zekri_ahmed.ip_tv_player.data.repository.ImageRepositoryImpl
import com.zekri_ahmed.ip_tv_player.data.repository.M3uRepositoryImpl
import com.zekri_ahmed.ip_tv_player.domain.notification.NotificationProvider
import com.zekri_ahmed.ip_tv_player.domain.repository.ImageRepository
import com.zekri_ahmed.ip_tv_player.domain.repository.M3uRepository
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import com.zekri_ahmed.ip_tv_player.domain.usecase.DownloadImageUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.GetLastLoadedPlaylistUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.LoadPlaylistUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.PlayMediaUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

    @Provides
    @Singleton
    fun provideDownloadImageUseCase(
        imageRepository: ImageRepository
    ): DownloadImageUseCase {
        return DownloadImageUseCase(imageRepository)
    }

    @Provides
    @Singleton
    fun provideGetLastLoadedUseCase(
        mu3uRepository: M3uRepository
    ): GetLastLoadedPlaylistUseCase {
        return GetLastLoadedPlaylistUseCase(mu3uRepository)
    }


    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context, // Provide the Context
        imageLoader: ImageLoader // Provide the ImageLoader
    ): ImageRepository {
        return ImageRepositoryImpl(context, imageLoader)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(NotificationManager::class.java)!!
    }

    @Provides
    @Singleton
    fun provideNotificationProvider(
        @ApplicationContext context: Context, // Provide the Context
        mediaSession: MediaSession, notificationManager: NotificationManager
    ): NotificationProvider {
        return NotificationProviderImpl(notificationManager, context, mediaSession)
    }
}
