package com.weatheradvisor.weather.analyzer.sheduler

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weatheradvisor.weather.analyzer.presentation.WeatherNotificationService
import com.weatheradvisor.weather.analyzer.repo.NotificationSettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherNotificationService: WeatherNotificationService,
    private val notificationSettingsRepository: NotificationSettingsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val cityId = notificationSettingsRepository.getSelectedCityId()
            if (cityId != -1) {
                weatherNotificationService.processWeatherNotifications(cityId)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherNotificationWorker", "Error processing notifications", e)
            Result.retry()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): WeatherNotificationWorker
    }
}