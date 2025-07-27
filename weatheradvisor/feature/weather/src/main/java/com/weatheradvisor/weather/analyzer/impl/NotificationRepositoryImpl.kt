package com.weatheradvisor.weather.analyzer.impl

import com.weatheradvisor.weather.analyzer.database.NotificationDao
import com.weatheradvisor.weather.analyzer.models.WeatherRecommendation
import com.weatheradvisor.weather.analyzer.models.mappers.toDomain
import com.weatheradvisor.weather.analyzer.models.mappers.toEntity
import com.weatheradvisor.weather.analyzer.repo.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val dao: NotificationDao
) : NotificationRepository {

    override  fun saveScheduledNotification(recommendation: WeatherRecommendation) {
        dao.insertNotification(recommendation.toEntity())
    }

    override  fun markAsSent(recommendationId: String) {
        dao.markAsSent(recommendationId)
    }

    override suspend fun getScheduledNotifications(): List<WeatherRecommendation> {
        return dao.getScheduledNotifications().map { it.toDomain() }
    }

    override suspend fun getSentNotifications(): List<WeatherRecommendation> {
        return dao.getSentNotifications().map { it.toDomain() }
    }
}