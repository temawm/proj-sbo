package com.weatheradvisor.weather.analyzer.presentation

import android.util.Log
import com.weatheradvisor.weather.analyzer.model.WeatherAnalyzer
import com.weatheradvisor.weather.analyzer.model.WeatherNotificationManager
import com.weatheradvisor.weather.analyzer.model.WeatherRecommendationGenerator
import com.weatheradvisor.weather.analyzer.models.RecommendationPriority
import com.weatheradvisor.weather.analyzer.models.RecommendationType
import com.weatheradvisor.weather.analyzer.models.WeatherAnalysis
import com.weatheradvisor.weather.analyzer.models.WeatherRecommendation
import com.weatheradvisor.weather.domain.usecases.GetWeatherForecastUseCase
import com.weatheradvisor.weather.domain.usecases.WeatherForecastResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherNotificationService @Inject constructor(
    private val weatherAnalyzer: WeatherAnalyzer,
    private val recommendationGenerator: WeatherRecommendationGenerator,
    private val notificationManager: WeatherNotificationManager,
    private val getWeatherUseCase: GetWeatherForecastUseCase
) {

    // Основной метод - возвращает рекомендации для UI
    suspend fun processWeatherNotifications(cityId: Int): List<WeatherRecommendation> {
        try {
            Log.d("WeatherNotificationService", "Processing notifications for city: $cityId")

            // Получаем прогноз погоды
            val weatherResult = getWeatherUseCase(cityId)
            Log.d("WeatherNotificationService", "Weather result: $weatherResult")

            if (weatherResult is WeatherForecastResult.Success) {
                Log.d("WeatherNotificationService", "Weather forecast received")

                // Анализируем погоду
                val analysis = weatherAnalyzer.analyzeWeatherForecast(weatherResult.forecast)
                Log.d("WeatherNotificationService", "Weather analysis completed")

                // Генерируем рекомендации
                val recommendations = recommendationGenerator.generateRecommendations(analysis)
                Log.d("WeatherNotificationService", "Generated ${recommendations.size} recommendations")

                if (recommendations.isNotEmpty()) {
                    // Планируем уведомления (фоновые push)
                    notificationManager.scheduleWeatherNotifications(recommendations)
                    Log.d("WeatherNotificationService", "Scheduled ${recommendations.size} notifications")
                }

                return recommendations
            } else {
                Log.e("WeatherNotificationService", "Weather result is not success: $weatherResult")
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("WeatherNotificationService", "Error processing weather notifications", e)
            return emptyList()
        }
    }
    // Получить анализ погоды без уведомлений
    suspend fun getWeatherAnalysis(cityId: Int): WeatherAnalysis? {
        try {
            val weatherResult = getWeatherUseCase(cityId)
            if (weatherResult is WeatherForecastResult.Success) {
                return weatherAnalyzer.analyzeWeatherForecast(weatherResult.forecast)
            }
        } catch (e: Exception) {
            Log.e("WeatherNotificationService", "Error getting weather analysis", e)
        }
        return null
    }
}


