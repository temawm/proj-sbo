package com.weatheradvisor.weather.analyzer.model

import com.weatheradvisor.weather.analyzer.models.DailySummary
import com.weatheradvisor.weather.analyzer.models.TemperatureRange
import com.weatheradvisor.weather.analyzer.models.TimeSlot
import com.weatheradvisor.weather.analyzer.models.TimeSlotAnalysis
import com.weatheradvisor.weather.analyzer.models.WeatherAnalysis
import com.weatheradvisor.weather.domain.models.WeatherForecast
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherAnalyzer @Inject constructor() {

    fun analyzeWeatherForecast(forecast: WeatherForecast): WeatherAnalysis {
        val groupedForecasts = groupForecastsByTimeSlot(forecast.forecasts)

        val morningAnalysis = analyzeTimeSlot(TimeSlot.MORNING, groupedForecasts[TimeSlot.MORNING] ?: emptyList())
        val dayAnalysis = analyzeTimeSlot(TimeSlot.DAY, groupedForecasts[TimeSlot.DAY] ?: emptyList())
        val eveningAnalysis = analyzeTimeSlot(TimeSlot.EVENING, groupedForecasts[TimeSlot.EVENING] ?: emptyList())

        val dailySummary = createDailySummary(listOf(morningAnalysis, dayAnalysis, eveningAnalysis))

        return WeatherAnalysis(
            morning = morningAnalysis,
            day = dayAnalysis,
            evening = eveningAnalysis,
            dailySummary = dailySummary
        )
    }

    private fun groupForecastsByTimeSlot(forecasts: List<WeatherForecast.DailyForecast>): Map<TimeSlot, List<WeatherForecast.DailyForecast>> {
        return forecasts.groupBy { forecast ->
            val hour = Calendar.getInstance().apply {
                timeInMillis = forecast.timestamp * 1000
            }.get(Calendar.HOUR_OF_DAY)

            when (hour) {
                in 6..11 -> TimeSlot.MORNING
                in 12..17 -> TimeSlot.DAY
                in 18..23, in 0..5 -> TimeSlot.EVENING
                else -> TimeSlot.DAY
            }
        }
    }

    private fun analyzeTimeSlot(timeSlot: TimeSlot, forecasts: List<WeatherForecast.DailyForecast>): TimeSlotAnalysis {
        if (forecasts.isEmpty()) {
            return TimeSlotAnalysis(
                timeSlot = timeSlot,
                avgTemperature = 0.0,
                minTemperature = 0.0,
                maxTemperature = 0.0,
                precipitationProbability = 0.0,
                windSpeed = 0.0,
                humidity = 0,
                conditions = emptyList()
            )
        }

        val temperatures = forecasts.map { it.temperature }
        val windSpeeds = forecasts.map { it.windSpeed }
        val humidities = forecasts.map { it.humidity }
        val conditions = forecasts.map { it.condition }

        val precipitationProbability = calculatePrecipitationProbability(conditions)

        return TimeSlotAnalysis(
            timeSlot = timeSlot,
            avgTemperature = temperatures.average(),
            minTemperature = temperatures.minOrNull() ?: 0.0,
            maxTemperature = temperatures.maxOrNull() ?: 0.0,
            precipitationProbability = precipitationProbability,
            windSpeed = windSpeeds.average(),
            humidity = humidities.average().toInt(),
            conditions = conditions
        )
    }

    private fun calculatePrecipitationProbability(conditions: List<WeatherForecast.WeatherCondition>): Double {
        val precipitationConditions = listOf("Rain", "Snow", "Drizzle", "Thunderstorm")
        val precipitationCount = conditions.count { condition ->
            precipitationConditions.any { it.lowercase() in condition.type.lowercase() }
        }
        return if (conditions.isNotEmpty()) precipitationCount.toDouble() / conditions.size else 0.0
    }

    private fun createDailySummary(analyses: List<TimeSlotAnalysis>): DailySummary {
        val allTemperatures = analyses.flatMap { listOf(it.minTemperature, it.maxTemperature) }
        val minTemp = allTemperatures.minOrNull() ?: 0.0
        val maxTemp = allTemperatures.maxOrNull() ?: 0.0
        val tempDifference = maxTemp - minTemp

        val precipitationExpected = analyses.any { it.precipitationProbability > 0.3 }
        val windyConditions = analyses.any { it.windSpeed > 5.0 }
        val temperatureFluctuations = tempDifference > 10.0

        return DailySummary(
            temperatureRange = TemperatureRange(minTemp, maxTemp, tempDifference),
            precipitationExpected = precipitationExpected,
            windyConditions = windyConditions,
            temperatureFluctuations = temperatureFluctuations
        )
    }
}