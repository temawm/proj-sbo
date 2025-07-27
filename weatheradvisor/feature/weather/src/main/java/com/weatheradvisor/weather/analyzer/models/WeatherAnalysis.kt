package com.weatheradvisor.weather.analyzer.models

import com.weatheradvisor.weather.domain.models.WeatherForecast


data class WeatherAnalysis(
    val morning: TimeSlotAnalysis,
    val day: TimeSlotAnalysis,
    val evening: TimeSlotAnalysis,
    val dailySummary: DailySummary
)

data class TimeSlotAnalysis(
    val timeSlot: TimeSlot,
    val avgTemperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val precipitationProbability: Double,
    val windSpeed: Double,
    val humidity: Int,
    val conditions: List<WeatherForecast.WeatherCondition>
)

data class DailySummary(
    val temperatureRange: TemperatureRange,
    val precipitationExpected: Boolean,
    val windyConditions: Boolean,
    val temperatureFluctuations: Boolean
)

data class TemperatureRange(
    val min: Double,
    val max: Double,
    val difference: Double
)

enum class TimeSlot {
    MORNING, DAY, EVENING
}