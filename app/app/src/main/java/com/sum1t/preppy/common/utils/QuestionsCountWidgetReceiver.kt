package com.sum1t.preppy.common.utils

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.sum1t.preppy.presentation.widget.QuestionsCountWidget

class QuestionsCountWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = QuestionsCountWidget()
}