package com.sum1t.preppy.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sum1t.preppy.data.worker.DailySchedulerWorker
import com.sum1t.preppy.domain.repository.notification.NotificationScheduler
import com.sum1t.preppy.domain.usecase.userpreferences.GetNotificationEnabledUseCase
import java.util.concurrent.TimeUnit

class NotificationSchedulerImpl(
    private val context: Context
) : NotificationScheduler {
    override fun scheduleDaily() {
        val request = PeriodicWorkRequestBuilder<DailySchedulerWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_scheduler",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}