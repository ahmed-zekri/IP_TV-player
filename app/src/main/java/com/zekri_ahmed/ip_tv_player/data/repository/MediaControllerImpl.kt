package com.zekri_ahmed.ip_tv_player.data.repository

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import com.zekri_ahmed.ip_tv_player.domain.repository.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@UnstableApi
class MediaControllerImpl @Inject constructor(
    private val player: ExoPlayer
) : MediaController {
    private var isPaused: Boolean = false
    private val playerState = MutableStateFlow(PlayerState())


    // Track currently playing channel
    private var currentMediaUrl: String = ""
    private var currentChannelPosition: MutableMap<String, Long> = mutableMapOf()

    init {
        setupPlayer()
    }

    private fun setupPlayer() {


        // Setup player listeners
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                updatePlayerState()

                // Save position when stopping to allow resuming later
                if (state == Player.STATE_IDLE) {
                    currentMediaUrl.takeIf { it.isNotEmpty() }?.let { url ->
                        currentChannelPosition[url] = playerState.value.currentPosition
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

    override fun play(mediaUrl: String, title: String) {
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

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun pause() {
        player.pause()
        isPaused = true
        updatePlayerState()
    }

    override fun resume() {
        player.play()
        isPaused = false
        updatePlayerState()
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun getState() = playerState.asStateFlow()


    private fun updatePlayerState() {
        val currentUrl = player.currentMediaItem?.localConfiguration?.uri.toString()

        playerState.value = PlayerState(
            player = player,
            isPlaying = player.isPlaying,
            currentPosition = player.currentPosition,
            bufferedPosition = player.bufferedPosition,
            m3uEntry = playerState.value.m3uEntry,
            isFullScreen = playerState.value.isFullScreen,
            isLoading = player.isLoading,
            playerError = player.playerError?.message,
            isPaused = isPaused
        )

        // Update the current media URL
        if (currentUrl.isNotEmpty()) {
            currentMediaUrl = currentUrl
        }
    }

}

