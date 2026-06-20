package com.sum1t.preppy.data.worker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sum1t.preppy.common.utils.StudyNotificationSpinner
import com.sum1t.preppy.data.broadcast.NotificationReceiver
import java.util.concurrent.TimeUnit

class DailySchedulerWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        repeat(4) { index ->
            val delay = getRandomDelay()
//            val delay = getRandomDelayTest()

            scheduleAlarm(index, delay)
        }
        return Result.success()
    }

    private fun getRandomDelay(): Long {
        val min = 1L
        val max = 12L
        val randomHour = (min..max).random()
        return TimeUnit.HOURS.toMillis(randomHour)
    }

    private fun getRandomDelayTest(): Long {
        return (0L..120_000L).random()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(index: Int, delay: Long) {

        val triggerAt = System.currentTimeMillis() + delay
        val notificationSpinner = StudyNotificationSpinner.getRandom()
        val intent = Intent(applicationContext, NotificationReceiver::class.java).apply {
            putExtra("title", notificationSpinner.title)
            putExtra("message", notificationSpinner.message)
            putExtra("id", index)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            index,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )

        Log.d("NotificationWorker", "Scheduled notification at $triggerAt")

    }

}


