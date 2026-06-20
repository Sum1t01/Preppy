//package com.sum1t.preppy.data.worker
//
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import androidx.core.app.NotificationCompat
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.sum1t.preppy.R
//import com.sum1t.preppy.presentation.MainActivity
//import kotlin.random.Random
//
//class NotificationWorker(
//    context: Context,
//    params: WorkerParameters
//) : Worker(context, params) {
//    override fun doWork(): Result {
//        val message = inputData.getString("message") ?: "Hello!"
//        showNotification(message)
//        return Result.success()
//
//    }
//
//    private fun showNotification(message: String) {
//
//        val manager =
//            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
//                    as NotificationManager
//
//        val intent = Intent(applicationContext, MainActivity::class.java)
//
//        val pendingIntent = PendingIntent.getActivity(
//            applicationContext,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification =
//            NotificationCompat.Builder(applicationContext, "channel_id")
//                .setContentTitle("Reminder")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .build()
//
//        manager.notify(Random.nextInt(), notification)
//    }
//
//}