package com.sum1t.preppy.data.usecase

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.sum1t.preppy.data.broadcast.NotificationReceiver
import com.sum1t.preppy.domain.repository.notification.NotificationScheduler
import com.sum1t.preppy.domain.usecase.SyncNotificationStateUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetNotificationEnabledUseCase
import kotlinx.coroutines.flow.distinctUntilChanged

class SyncNotificationStateUseCaseImpl(
    private val getNotificationEnabledUseCase: GetNotificationEnabledUseCase,
    private val notificationScheduler: NotificationScheduler,
    private val context: Context
) : SyncNotificationStateUseCase {
    override suspend fun observe(context: Context) {
        getNotificationEnabledUseCase.invoke()
            .distinctUntilChanged()
            .collect { isEnabled ->
                if (isEnabled) {
                    notificationScheduler.scheduleDaily()
                } else {
                    cancelAll(context)
                }
            }
    }

    private fun cancelAll(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("daily_scheduler")

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        repeat(4) { index ->

            val intent = Intent(context, NotificationReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)

        }
    }
}