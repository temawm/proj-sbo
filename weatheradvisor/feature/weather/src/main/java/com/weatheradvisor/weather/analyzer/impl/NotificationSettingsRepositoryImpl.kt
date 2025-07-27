package com.weatheradvisor.weather.analyzer.impl

import android.content.SharedPreferences
import com.weatheradvisor.weather.analyzer.repo.NotificationSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSettingsRepositoryImpl @Inject constructor(
    private val preferences: SharedPreferences
) : NotificationSettingsRepository {

    override suspend fun getSelectedCityId(): Int {
        return preferences.getInt("selected_city_id", -1)
    }

    override suspend fun setSelectedCityId(cityId: Int) {
        preferences.edit().putInt("selected_city_id", cityId).apply()
    }

    override suspend fun isNotificationsEnabled(): Boolean {
        return preferences.getBoolean("notifications_enabled", true)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        preferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    override suspend fun getNotificationTime(): String {
        return preferences.getString("notification_time", "08:00") ?: "08:00"
    }

    override suspend fun setNotificationTime(time: String) {
        preferences.edit().putString("notification_time", time).apply()
    }
}