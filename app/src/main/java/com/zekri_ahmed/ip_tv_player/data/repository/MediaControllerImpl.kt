package com.zekri_ahmed.ip_tv_player.data.repository

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@UnstableApi
class MediaControllerImpl @Inject constructor(
    private val player: ExoPlayer
) : MediaController {
    private val _playerState = MutableStateFlow(PlayerState())


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
    }

    override fun resume() {
        player.play()
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    private fun updatePlayerState() {
        val currentUrl = player.currentMediaItem?.localConfiguration?.uri.toString()

        _playerState.value = PlayerState(
            isPlaying = player.isPlaying,
            currentPosition = player.currentPosition,
            bufferedPosition = player.bufferedPosition,
            duration = player.duration,
            currentMediaUrl = currentUrl,
            title = player.currentMediaItem?.mediaMetadata?.title?.toString() ?: "",
            isFullScreen = _playerState.value.isFullScreen
        )

        // Update the current media URL
        if (currentUrl.isNotEmpty()) {
            currentMediaUrl = currentUrl
        }
    }

}

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val bufferedPosition: Long = 0,
    val duration: Long = 0,
    val currentMediaUrl: String = "",
    val title: String = "",
    val isFullScreen: Boolean = false
)