package com.weatheradvisor.network.data.api

import com.weatheradvisor.network.data.models.WeatherForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("id") cityId: Int,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru"
    ): Response<WeatherForecastResponse>
}