package com.zekri_ahmed.ip_tv_player.domain.notification

import android.app.Notification
import android.graphics.Bitmap

interface NotificationProvider {
    suspend fun updateNotification(title: String, largeIcon: Bitmap? = null): Notification
}