package com.zekri_ahmed.ip_tv_player.data.repository

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.zekri_ahmed.ip_tv_player.domain.repository.M3uRepository
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import com.zekri_ahmed.ip_tv_player.domain.repository.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@UnstableApi
class MediaControllerImpl @Inject constructor(
    private val player: ExoPlayer,
    private val m3uRepository: M3uRepository
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

    override fun play(index: Int) {
        player.seekTo(index, 0)
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
        if (player.mediaItemCount == 0)
            m3uRepository.getLastLoadedPlaylist()?.let { m3uList ->
                player.setMediaItems(m3uList.map { m3uEntry ->
                    MediaItem.Builder()
                        .setUri(m3uEntry.path)
                        .setMediaId(m3uEntry.path)
                        .apply {
                            if (m3uEntry.title.isNotEmpty()) {
                                setMediaMetadata(
                                    androidx.media3.common.MediaMetadata.Builder()
                                        .setTitle(m3uEntry.title)
                                        .build()
                                )
                            }
                        }
                        .build()

                })
            }

        val currentUrl = player.currentMediaItem?.localConfiguration?.uri.toString()

        playerState.value = PlayerState(
            player = player,
            isPlaying = player.isPlaying,
            currentPosition = player.currentPosition,
            bufferedPosition = player.bufferedPosition,
            currentIndex = m3uRepository.getLastLoadedPlaylist()
                ?.indexOfFirst { it.path == player.currentMediaItem?.localConfiguration?.uri?.toString() }
                ?: -1,
            currentM3uEntries = m3uRepository.getLastLoadedPlaylist() ?: emptyList(),
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

