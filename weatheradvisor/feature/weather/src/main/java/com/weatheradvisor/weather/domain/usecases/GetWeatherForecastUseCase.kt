package com.weatheradvisor.weather.domain.usecases

import com.weatheradvisor.network.domain.ForecastResult
import com.weatheradvisor.network.domain.WeatherRepository
import com.weatheradvisor.weather.domain.mappers.toDomain
import com.weatheradvisor.weather.domain.models.WeatherForecast
import javax.inject.Inject

sealed class WeatherForecastResult {
    data class Success(val forecast: WeatherForecast) : WeatherForecastResult()
    data class Error(val code: Int, val message: String?) : WeatherForecastResult()
}

class GetWeatherForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cityId: Int): WeatherForecastResult {
        return when (val result = repository.getForecast(cityId)) {
            is ForecastResult.Success -> {
                result.data?.let { data ->
                    WeatherForecastResult.Success(data.toDomain())
                } ?: WeatherForecastResult.Error(-5, "Нет данных о погоде")
            }
            is ForecastResult.Error -> {
                WeatherForecastResult.Error(result.code, result.message)
            }
            else -> WeatherForecastResult.Error(-7, "Неожиданный результат")
        }
    }
}