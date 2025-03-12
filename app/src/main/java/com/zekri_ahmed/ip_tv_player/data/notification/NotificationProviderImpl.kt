// Framework Layer
package com.zekri_ahmed.ip_tv_player.data.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.zekri_ahmed.ip_tv_player.MainActivity
import com.zekri_ahmed.ip_tv_player.data.CHANNEL_ID
import com.zekri_ahmed.ip_tv_player.data.NOTIFICATION_ID
import com.zekri_ahmed.ip_tv_player.domain.notification.NotificationProvider
import javax.inject.Inject

class NotificationProviderImpl @Inject constructor(
    private val notificationManager: NotificationManager,
    private val context: Context,
    private val mediaSession: MediaSession
) : NotificationProvider {

    override suspend fun updateNotification(title: String, largeIcon: Bitmap?) =
        createNotification(title, largeIcon).apply {
            notificationManager.notify(NOTIFICATION_ID, this)
        }


    @OptIn(UnstableApi::class)
    private fun createNotification(title: String, largeIcon: Bitmap?): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(androidx.media3.session.R.drawable.media_session_service_notification_ic_music_note)
            .setContentTitle(title)
            .setContentText("Media playback active")
            .setLargeIcon(largeIcon) // Set the large icon here
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }


}