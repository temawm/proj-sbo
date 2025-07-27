package com.weatheradvisor.weather.analyzer.models.mappers

import com.weatheradvisor.weather.analyzer.database.entity.NotificationEntity
import com.weatheradvisor.weather.analyzer.models.RecommendationPriority
import com.weatheradvisor.weather.analyzer.models.RecommendationType
import com.weatheradvisor.weather.analyzer.models.WeatherRecommendation

fun WeatherRecommendation.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        message = message,
        priority = priority.name,
        scheduledTime = scheduledTime,
        type = type.name,
        isScheduled = isScheduled,
        isSent = isSent
    )
}

fun NotificationEntity.toDomain(): WeatherRecommendation {
    return WeatherRecommendation(
        id = id,
        message = message,
        priority = RecommendationPriority.valueOf(priority),
        scheduledTime = scheduledTime,
        type = RecommendationType.valueOf(type),
        isScheduled = isScheduled,
        isSent = isSent
    )
}
