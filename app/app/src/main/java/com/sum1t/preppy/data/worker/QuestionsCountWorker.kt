package com.sum1t.preppy.data.worker

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.data.usecase.question.GetAttemptedQuestionsCountUseCaseImpl
import com.sum1t.preppy.data.usecase.question.GetTotalQuestionsCountUseCaseImpl
import com.sum1t.preppy.presentation.widget.QuestionsCountWidget
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class QuestionsCountWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val getAttemptedQuestionsCountUseCase: GetAttemptedQuestionsCountUseCaseImpl by inject()
    private val getTotalQuestionsCountUseCase: GetTotalQuestionsCountUseCaseImpl by inject()

    override suspend fun doWork(): Result = coroutineScope {

        val context = applicationContext

        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(QuestionsCountWidget::class.java)

        if (glanceIds.isEmpty()) return@coroutineScope Result.success()

        val totalKey = longPreferencesKey("total_questions")
        val attemptedKey = longPreferencesKey("attempted_questions")
        val loadingKey = booleanPreferencesKey("loading")
        val errorKey = stringPreferencesKey("error")

        // 🔄 1. Set loading state
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[loadingKey] = true
                prefs[errorKey] = ""
            }
        }
        QuestionsCountWidget().updateAll(context)

        try {
            // ⚡ 2. Run both API calls in parallel
            val attemptedDeferred = async {
                getAttemptedQuestionsCountUseCase.invoke().last()
            }

            val totalDeferred = async {
                getTotalQuestionsCountUseCase.invoke().last()
            }

            val attemptedResult = attemptedDeferred.await()
            val totalResult = totalDeferred.await()

            var attempted = 0L
            var total = 0L
            var error: String? = null

            // 📊 Process attempted result
            attempted = attemptedResult

            // 📊 Process total result
            total = totalResult


            // ✅ 3. Update success state
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[attemptedKey] = attempted
                    prefs[totalKey] = total
                    prefs[loadingKey] = false
                    prefs[errorKey] = error ?: ""
                }
            }

            QuestionsCountWidget().updateAll(context)

            Result.success()

        } catch (e: Exception) {

            // ❌ 4. Update error state
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[loadingKey] = false
                    prefs[errorKey] = e.message ?: "Unknown error"
                }
            }

            QuestionsCountWidget().updateAll(context)

            Result.failure()
        }
    }
}