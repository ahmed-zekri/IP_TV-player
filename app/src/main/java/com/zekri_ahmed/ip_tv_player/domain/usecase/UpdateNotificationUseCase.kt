// Domain Layer
package com.zekri_ahmed.ip_tv_player.domain.usecase

import android.graphics.Bitmap
import com.zekri_ahmed.ip_tv_player.domain.notification.NotificationProvider
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val notificationProvider: NotificationProvider
) {
    suspend operator fun invoke(title: String, largeIcon: Bitmap? = null) =
        notificationProvider.updateNotification(title, largeIcon)

}