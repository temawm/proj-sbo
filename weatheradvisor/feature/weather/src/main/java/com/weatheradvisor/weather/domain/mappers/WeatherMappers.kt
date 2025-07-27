package com.weatheradvisor.weather.domain.mappers

import com.weatheradvisor.network.data.models.*
import com.weatheradvisor.weather.domain.models.WeatherForecast

// Основной маппер для всего ответа
fun WeatherForecastResponse.toDomain(): WeatherForecast {
    return WeatherForecast(
        cityId = city.id,
        cityName = city.name,
        countryCode = city.country,
        forecasts = list.map { it.toDomain() }
    )
}

// Маппер для отдельного прогноза
private fun Forecast.toDomain(): WeatherForecast.DailyForecast {
    return WeatherForecast.DailyForecast(
        timestamp = dt,
        temperature = main.temp,
        pressure = main.pressure,
        humidity = main.humidity,
        windSpeed = wind.speed,
        windDirection = wind.deg,
        condition = weather.firstOrNull()?.toDomain() ?: defaultWeatherCondition()
    )
}

// Маппер для погодных условий
private fun Weather.toDomain(): WeatherForecast.WeatherCondition {
    return WeatherForecast.WeatherCondition(
        type = main,
        description = description,
        iconCode = icon
    )
}

// Дефолтное значение для случая отсутствия weather data
private fun defaultWeatherCondition() = WeatherForecast.WeatherCondition(
    type = "Unknown",
    description = "No weather data",
    iconCode = ""
)