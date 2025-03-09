package com.zekri_ahmed.ip_tv_player.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.exoplayer.ExoPlayer
import com.zekri_ahmed.ip_tv_player.domain.model.M3uEntry
import com.zekri_ahmed.ip_tv_player.domain.repository.M3uRepository
import com.zekri_ahmed.ip_tv_player.domain.repository.MediaController
import com.zekri_ahmed.ip_tv_player.domain.usecase.LoadPlaylistUseCase
import com.zekri_ahmed.ip_tv_player.domain.usecase.PlayMediaUseCase
import com.zekri_ahmed.ip_tv_player.presentation.viewmodel.MainViewModel


@Composable
@Preview
fun MainScreenPreview() {
    val viewModel = MainViewModel(
        LoadPlaylistUseCase(object : M3uRepository {
            override suspend fun loadPlaylist(filePath: String): List<M3uEntry> =
                listOf()

        }),
        playMediaUseCase = PlayMediaUseCase(object : MediaController {
            override fun play(mediaUrl: String, title: String) {

            }

            override fun pause() {

            }

            override fun resume() {

            }

            override fun seekTo(position: Long) {

            }

            override fun getPlayer(): ExoPlayer? = null
        }),
    )
    MainScreen(viewModel)


}