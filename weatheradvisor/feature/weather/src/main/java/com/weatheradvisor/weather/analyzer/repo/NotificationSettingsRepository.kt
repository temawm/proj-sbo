package com.weatheradvisor.weather.analyzer.repo

interface NotificationSettingsRepository {
    suspend fun getSelectedCityId(): Int
    suspend fun setSelectedCityId(cityId: Int)
    suspend fun isNotificationsEnabled(): Boolean
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun getNotificationTime(): String
    suspend fun setNotificationTime(time: String)
}