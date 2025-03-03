// app/src/main/java/com/example/m3uplayer/service/MediaPlayerService.kt

package com.zekri_ahmed.ip_tv_player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@UnstableApi
class MediaPlayerService : Service() {
    private val binder = MediaPlayerBinder()
    lateinit var player: ExoPlayer
    private lateinit var cache: SimpleCache

    // Current player state
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState

    // Buffer management
    private val bufferSize = 1024 * 1024 * 500 // 500MB cache for multiple channels

    // Track currently playing channel
    private var currentMediaUrl: String = ""
    private var currentChannelPosition: MutableMap<String, Long> = mutableMapOf()

    data class PlayerState(
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0,
        val bufferedPosition: Long = 0,
        val duration: Long = 0,
        val currentMediaUrl: String = "",
        val title: String = ""
    )

    inner class MediaPlayerBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        setupCache()
        setupPlayer()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun setupCache() {
        val cacheDir = File(cacheDir, "media_cache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val evictor = LeastRecentlyUsedCacheEvictor(bufferSize.toLong())
        cache = SimpleCache(cacheDir, evictor)
    }

    private fun setupPlayer() {
        // Setup data source factory with cache
        val dataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(
                DefaultDataSource.Factory(this)
            )

        // Create media source factory
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        // Create player
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        // Setup player listeners
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                updatePlayerState()

                // Save position when stopping to allow resuming later
                if (state == Player.STATE_IDLE) {
                    currentMediaUrl.takeIf { it.isNotEmpty() }?.let { url ->
                        currentChannelPosition[url] = _playerState.value.currentPosition
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlayerState()
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updatePlayerState()
            }
        })
    }

    private fun updatePlayerState() {
        val currentUrl = player.currentMediaItem?.localConfiguration?.uri.toString()

        _playerState.value = PlayerState(
            isPlaying = player.isPlaying,
            currentPosition = player.currentPosition,
            bufferedPosition = player.bufferedPosition,
            duration = player.duration,
            currentMediaUrl = currentUrl,
            title = player.currentMediaItem?.mediaMetadata?.title?.toString() ?: ""
        )

        // Update the current media URL
        if (currentUrl.isNotEmpty()) {
            currentMediaUrl = currentUrl
        }
    }

    // Media player controls
    fun play(mediaUrl: String, title: String = "") {
        // Save current position of the currently playing channel
        if (currentMediaUrl.isNotEmpty() && currentMediaUrl != mediaUrl) {
            currentChannelPosition[currentMediaUrl] = player.currentPosition
        }

        val mediaItem = MediaItem.Builder()
            .setUri(mediaUrl)
            .setMediaId(mediaUrl)
            .apply {
                if (title.isNotEmpty()) {
                    setMediaMetadata(
                        androidx.media3.common.MediaMetadata.Builder()
                            .setTitle(title)
                            .build()
                    )
                }
            }
            .build()

        // Check if we need to resume from a previous position
        val savedPosition = currentChannelPosition[mediaUrl] ?: 0L

        // If same media is already playing, don't reload
        if (currentMediaUrl == mediaUrl && player.isPlaying) {
            return
        }

        // If same media is loaded but not playing, just resume
        if (currentMediaUrl == mediaUrl && !player.isPlaying) {
            player.play()
            return
        }

        // Otherwise load the new media
        player.setMediaItem(mediaItem)
        player.prepare()

        // Seek to saved position if available
        if (savedPosition > 0) {
            player.seekTo(savedPosition)
        }

        player.play()
        currentMediaUrl = mediaUrl

        // Update notification with new channel info
        updateNotification(title)
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        player.play()
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        player.release()
        cache.release()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Media Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Media playback controls"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(title: String = "M3U Player"): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(androidx.media3.ui.R.drawable.notification_bg)
            .setContentTitle(title)
            .setContentText("Media playback active")
            .build()
    }

    private fun updateNotification(title: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(title))
    }

    companion object {
        private const val CHANNEL_ID = "media_playback_channel"
        private const val NOTIFICATION_ID = 1
    }
}