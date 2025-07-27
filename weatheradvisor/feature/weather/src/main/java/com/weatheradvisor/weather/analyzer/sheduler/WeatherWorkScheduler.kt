package com.weatheradvisor.weather.analyzer.sheduler

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherWorkScheduler @Inject constructor(
    private val workManager: WorkManager
) {

    fun schedulePeriodicWeatherNotifications() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(
            6, TimeUnit.HOURS // Обновляем каждые 6 часов
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "weather_notifications",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWork
        )
    }

    fun cancelPeriodicWeatherNotifications() {
        workManager.cancelUniqueWork("weather_notifications")
    }
}