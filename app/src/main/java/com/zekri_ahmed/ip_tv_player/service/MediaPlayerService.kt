package com.zekri_ahmed.ip_tv_player.service

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.zekri_ahmed.ip_tv_player.data.NOTIFICATION_ID
import com.zekri_ahmed.ip_tv_player.domain.usecase.DownloadImageUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.PlayMediaUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.UpdateNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MediaPlayerService : MediaSessionService() {

    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var cache: Cache

    @Inject
    lateinit var downloadImageUseCase: DownloadImageUseCase

    @Inject
    lateinit var updateNotificationUseCase: UpdateNotificationUseCase

    @Inject
    lateinit var playMediaUseCase: PlayMediaUseCase


    @Inject
    lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        // Start the service in the foreground with the media notification
        CoroutineScope(Dispatchers.Main).launch {
            startForeground(NOTIFICATION_ID, updateNotificationUseCase("Loading playlist...", null))

            playMediaUseCase.playerState.collect { state ->
                state.currentM3uEntries.getOrNull(state.currentIndex)?.let { m3uEntry ->

                    m3uEntry.thumbnailUrl?.let { logoUrl ->
                        downloadImageUseCase(logoUrl)?.let { bitmap ->
                            updateNotificationUseCase(m3uEntry.title, bitmap)

                        }


                    }


                }


            }
        }
    }

    override fun onDestroy() {
        player.release()
        cache.release()
        mediaSession.release()
        super.onDestroy()
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

}
