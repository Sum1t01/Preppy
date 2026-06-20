package com.sum1t.preppy.domain.usecase

import com.sum1t.preppy.domain.repository.notification.NotificationScheduler

class ScheduleNotificationsUseCase(
    private val notificationScheduler: NotificationScheduler
) {
    fun invoke() {
        notificationScheduler.scheduleDaily()
    }
}