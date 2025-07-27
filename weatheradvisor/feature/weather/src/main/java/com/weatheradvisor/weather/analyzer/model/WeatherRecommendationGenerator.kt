package com.weatheradvisor.weather.analyzer.model

import com.weatheradvisor.weather.analyzer.models.RecommendationPriority
import com.weatheradvisor.weather.analyzer.models.RecommendationType
import com.weatheradvisor.weather.analyzer.models.WeatherAnalysis
import com.weatheradvisor.weather.analyzer.models.WeatherRecommendation
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRecommendationGenerator @Inject constructor() {

    fun generateRecommendations(analysis: WeatherAnalysis): List<WeatherRecommendation> {
        val recommendations = mutableListOf<WeatherRecommendation>()

        // Рекомендации по осадкам
        if (analysis.dailySummary.precipitationExpected) {
            recommendations.add(createPrecipitationRecommendation(analysis))
        }

        // Рекомендации по температуре
        if (analysis.dailySummary.temperatureFluctuations) {
            recommendations.add(createTemperatureFluctuationRecommendation(analysis))
        }

        // Рекомендации по ветру
        if (analysis.dailySummary.windyConditions) {
            recommendations.add(createWindRecommendation(analysis))
        }

        // Общие рекомендации для хорошей погоды
        if (!analysis.dailySummary.precipitationExpected &&
            !analysis.dailySummary.windyConditions &&
            analysis.day.avgTemperature in 18.0..28.0) {
            recommendations.add(createNiceWeatherRecommendation(analysis))
        }

        // Рекомендации по времени суток
        recommendations.addAll(generateTimeSpecificRecommendations(analysis))

        return recommendations
    }

    private fun createPrecipitationRecommendation(analysis: WeatherAnalysis): WeatherRecommendation {
        val message = when {
            analysis.morning.precipitationProbability > 0.5 -> "Утром ожидается дождь. Возьмите зонт!"
            analysis.day.precipitationProbability > 0.5 -> "Днём возможен дождь. Не забудьте зонт!"
            analysis.evening.precipitationProbability > 0.5 -> "Вечером ожидается дождь. Планируйте дорогу домой заранее!"
            else -> "Возможны осадки. Лучше взозьмите зонт на всякий случай!"
        }

        return WeatherRecommendation(
            id = "precipitation_${System.currentTimeMillis()}",
            message = message,
            priority = RecommendationPriority.HIGH,
            scheduledTime = getOptimalNotificationTime(analysis),
            type = RecommendationType.PRECIPITATION
        )
    }

    private fun createTemperatureFluctuationRecommendation(analysis: WeatherAnalysis): WeatherRecommendation {
        val tempDiff = analysis.dailySummary.temperatureRange.difference
        val message = when {
            tempDiff > 15 -> "Сегодня большой перепад температур (${tempDiff.toInt()}°C). Оденьтесь многослойно!"
            analysis.morning.avgTemperature < 15 && analysis.day.avgTemperature > 20 ->
                "Утром прохладно (${analysis.morning.avgTemperature.toInt()}°C), днём потеплеет до ${analysis.day.avgTemperature.toInt()}°C. Оденьтесь многослойно!"
            else -> "Перепад температур ${tempDiff.toInt()}°C. Выбирай одежду внимательней!"
        }

        return WeatherRecommendation(
            id = "temperature_${System.currentTimeMillis()}",
            message = message,
            priority = RecommendationPriority.MEDIUM,
            scheduledTime = getMorningNotificationTime(),
            type = RecommendationType.TEMPERATURE
        )
    }

    private fun createWindRecommendation(analysis: WeatherAnalysis): WeatherRecommendation {
        val maxWindSpeed = maxOf(analysis.morning.windSpeed, analysis.day.windSpeed, analysis.evening.windSpeed)
        val message = when {
            maxWindSpeed > 10 -> "Сильный ветер до ${maxWindSpeed.toInt()} м/с. Осторожней на улице!"
            maxWindSpeed > 7 -> "Ветрено (${maxWindSpeed.toInt()} м/с). Одевайтесь теплее!"
            else -> "Умеренный ветер. Лёгкая куртка не помешает!"
        }

        return WeatherRecommendation(
            id = "wind_${System.currentTimeMillis()}",
            message = message,
            priority = RecommendationPriority.MEDIUM,
            scheduledTime = getOptimalNotificationTime(analysis),
            type = RecommendationType.WIND
        )
    }

    private fun createNiceWeatherRecommendation(analysis: WeatherAnalysis): WeatherRecommendation {
        val messages = listOf(
            "Прекрасный день для прогулки! ☀️",
            "Отличная погода для активного отдыха!",
            "Солнечно и тепло. Идеальное время для прогулки!",
            "Замечательная погода! Не сидите дома! 🌞"
        )

        return WeatherRecommendation(
            id = "nice_weather_${System.currentTimeMillis()}",
            message = messages.random(),
            priority = RecommendationPriority.LOW,
            scheduledTime = getMorningNotificationTime(),
            type = RecommendationType.GENERAL
        )
    }

    private fun generateTimeSpecificRecommendations(analysis: WeatherAnalysis): List<WeatherRecommendation> {
        val recommendations = mutableListOf<WeatherRecommendation>()

        // Утренние рекомендации
        if (analysis.morning.avgTemperature < 5) {
            recommendations.add(
                WeatherRecommendation(
                id = "morning_cold_${System.currentTimeMillis()}",
                message = "Утром холодно (${analysis.morning.avgTemperature.toInt()}°C). Одевайся теплее!",
                priority = RecommendationPriority.MEDIUM,
                scheduledTime = getMorningNotificationTime(),
                type = RecommendationType.TEMPERATURE
            )
            )
        }

        // Дневные рекомендации
        if (analysis.day.avgTemperature > 30) {
            recommendations.add(
                WeatherRecommendation(
                id = "day_hot_${System.currentTimeMillis()}",
                message = "Днём жарко (${analysis.day.avgTemperature.toInt()}°C). Не забудь воду и головной убор!",
                priority = RecommendationPriority.HIGH,
                scheduledTime = getMorningNotificationTime(),
                type = RecommendationType.TEMPERATURE
            )
            )
        }

        return recommendations
    }

    private fun getOptimalNotificationTime(analysis: WeatherAnalysis): Long {
        // Если дождь утром - уведомление в 7:00
        // Если дождь днём - уведомление в 11:00
        // Если дождь вечером - уведомление в 16:00
        val calendar = Calendar.getInstance()

        when {
            analysis.morning.precipitationProbability > 0.4 -> {
                calendar.set(Calendar.HOUR_OF_DAY, 7)
                calendar.set(Calendar.MINUTE, 0)
            }
            analysis.day.precipitationProbability > 0.4 -> {
                calendar.set(Calendar.HOUR_OF_DAY, 11)
                calendar.set(Calendar.MINUTE, 0)
            }
            analysis.evening.precipitationProbability > 0.4 -> {
                calendar.set(Calendar.HOUR_OF_DAY, 16)
                calendar.set(Calendar.MINUTE, 0)
            }
            else -> {
                calendar.set(Calendar.HOUR_OF_DAY, 8)
                calendar.set(Calendar.MINUTE, 0)
            }
        }

        return calendar.timeInMillis
    }

    private fun getMorningNotificationTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }
}