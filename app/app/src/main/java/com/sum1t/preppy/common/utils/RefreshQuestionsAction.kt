package com.sum1t.preppy.common.utils

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sum1t.preppy.data.worker.QuestionsCountWorker
import kotlin.jvm.java

class RefreshQuestionsAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequest.from(QuestionsCountWorker::class.java))
    }
}