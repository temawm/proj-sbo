package com.weatheradvisor.weather.analyzer.repo

import com.weatheradvisor.weather.analyzer.models.WeatherRecommendation

interface NotificationRepository {
     fun saveScheduledNotification(recommendation: WeatherRecommendation)
     fun markAsSent(recommendationId: String)
    suspend fun getScheduledNotifications(): List<WeatherRecommendation>
    suspend fun getSentNotifications(): List<WeatherRecommendation>
}