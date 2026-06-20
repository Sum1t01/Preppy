package com.sum1t.preppy.data.broadcast

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.sum1t.preppy.R
import com.sum1t.preppy.presentation.MainActivity
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "✏️ Revision Time!"
        val message =
            intent?.getStringExtra("message") ?: "Refresh your memory with a quick practice round."

        val manager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("notif_id", Random.nextInt())
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification =
            NotificationCompat.Builder(context, "channel_id")
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        manager.notify(Random.nextInt(), notification)
    }
}