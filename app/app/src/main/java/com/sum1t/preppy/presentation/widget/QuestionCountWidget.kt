package com.sum1t.preppy.presentation.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.sum1t.preppy.common.utils.RefreshQuestionsAction
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences

class QuestionsCountWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                QuestionsContent()
            }
        }
    }
}

@Composable
fun QuestionsContent() {

    val prefs = currentState<Preferences>()

    val total = prefs[longPreferencesKey("total_questions")] ?: 0L
    val attempted = prefs[longPreferencesKey("attempted_questions")] ?: 0L
    val loading = prefs[booleanPreferencesKey("loading")] ?: false
    val error = prefs[stringPreferencesKey("error")] ?: ""

    val progress = if (total > 0) {
        (attempted * 100 / total).toInt()
    } else 0

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Progress",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(GlanceModifier.height(8.dp))

        when {
            loading -> {
                Text("Loading…", style = TextStyle(fontSize = 14.sp))
            }

            error.isNotEmpty() -> {
                Text(
                    text = "⚠",
                    style = TextStyle(fontSize = 20.sp)
                )
                Spacer(GlanceModifier.height(4.dp))
                Text(error, style = TextStyle(fontSize = 12.sp))
            }

            else -> {

                // 🔵 Fake circular progress using text focus
                Text(
                    text = "$progress%",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(GlanceModifier.height(4.dp))

                Text(
                    text = "$attempted / $total",
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }

        Spacer(GlanceModifier.height(10.dp))

        Text(
            text = "↻",
            style = TextStyle(fontSize = 18.sp),
            modifier = GlanceModifier.clickable(
                actionRunCallback<RefreshQuestionsAction>()
            )
        )
    }
}