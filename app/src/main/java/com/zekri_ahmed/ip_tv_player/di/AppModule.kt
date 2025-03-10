package com.zekri_ahmed.ip_tv_player.di

import android.content.Context
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

}

