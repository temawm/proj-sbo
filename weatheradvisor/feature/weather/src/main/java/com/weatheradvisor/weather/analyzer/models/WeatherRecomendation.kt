package com.weatheradvisor.weather.analyzer.models

data class WeatherRecommendation(
    val id: String,
    val message: String,
    val priority: RecommendationPriority,
    val scheduledTime: Long,
    val type: RecommendationType,
    val isScheduled: Boolean = false,
    val isSent: Boolean = false
)

enum class RecommendationPriority {
    LOW, MEDIUM, HIGH
}

enum class RecommendationType {
    PRECIPITATION, TEMPERATURE, WIND, GENERAL
}