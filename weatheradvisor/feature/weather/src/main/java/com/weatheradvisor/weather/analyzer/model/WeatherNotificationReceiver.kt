package com.weatheradvisor.weather.analyzer.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.weatheradvisor.weather.analyzer.di.WeatherNotificationManagerEntryPoint
import com.weatheradvisor.weather.analyzer.models.RecommendationPriority
import com.weatheradvisor.weather.analyzer.models.RecommendationType
import com.weatheradvisor.weather.analyzer.models.WeatherRecommendation
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        val recommendationId = intent.getStringExtra("recommendation_id")
        val message = intent.getStringExtra("message")
        val priorityString = intent.getStringExtra("priority")

        if (recommendationId == null || message == null || priorityString == null) {
            pendingResult.finish()
            return
        }

        val recommendation = WeatherRecommendation(
            id = recommendationId,
            message = message,
            priority = RecommendationPriority.valueOf(priorityString),
            scheduledTime = System.currentTimeMillis(),
            type = RecommendationType.GENERAL
        )

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WeatherNotificationManagerEntryPoint::class.java
        )
        val notificationManager = entryPoint.getWeatherNotificationManager()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                notificationManager.showNotification(recommendation)
            } finally {
                pendingResult.finish()
            }
        }
    }
}