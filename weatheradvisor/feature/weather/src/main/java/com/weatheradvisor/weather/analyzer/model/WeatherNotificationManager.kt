package com.weatheradvisor.weather.analyzer.model

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.weatheradvisor.weather.R
import com.weatheradvisor.weather.analyzer.models.RecommendationPriority
import com.weatheradvisor.weather.analyzer.models.WeatherRecommendation
import com.weatheradvisor.weather.analyzer.repo.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherNotificationManager @Inject constructor(
    private val context: Context,
    private val notificationRepository: NotificationRepository
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    suspend fun scheduleWeatherNotifications(recommendations: List<WeatherRecommendation>) {
        recommendations.forEach { recommendation ->
            scheduleNotification(recommendation)
        }
    }

    private suspend fun scheduleNotification(recommendation: WeatherRecommendation) {
        val intent = Intent(context, WeatherNotificationReceiver::class.java).apply {
            putExtra("recommendation_id", recommendation.id)
            putExtra("message", recommendation.message)
            putExtra("priority", recommendation.priority.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            recommendation.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Планируем уведомление
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                recommendation.scheduledTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                recommendation.scheduledTime,
                pendingIntent
            )
        }

        // Сохраняем в базу данных
        notificationRepository.saveScheduledNotification(
            recommendation.copy(isScheduled = true)
        )
    }

    fun showNotification(recommendation: WeatherRecommendation) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Погодная рекомендация")
            .setContentText(recommendation.message)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setPriority(getPriorityLevel(recommendation.priority))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(recommendation.id.hashCode(), notification)

        // Отмечаем как отправленное
        notificationRepository.markAsSent(recommendation.id)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Погодные рекомендации",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления с рекомендациями по погоде"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPriorityLevel(priority: RecommendationPriority): Int {
        return when (priority) {
            RecommendationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            RecommendationPriority.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            RecommendationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
        }
    }

    companion object {
        private const val CHANNEL_ID = "weather_recommendations"
    }
}