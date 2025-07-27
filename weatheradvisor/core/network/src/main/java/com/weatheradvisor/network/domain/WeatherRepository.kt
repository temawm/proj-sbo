package com.weatheradvisor.network.domain

import com.weatheradvisor.network.data.models.WeatherForecastResponse

// WeatherRepository.kt
interface WeatherRepository {
    suspend fun getForecast(cityId: Int): ForecastResult
}
