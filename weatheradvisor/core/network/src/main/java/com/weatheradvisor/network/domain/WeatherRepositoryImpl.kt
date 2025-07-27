package com.weatheradvisor.network.domain

import com.weatheradvisor.network.data.api.WeatherApiService
import com.weatheradvisor.network.data.models.WeatherForecastResponse
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

sealed class ForecastResult {
    data class Success(val data: WeatherForecastResponse?) : ForecastResult()
    data class Error(val code: Int, val message: String?) : ForecastResult()
    object Loading : ForecastResult()
}

// WeatherRepositoryImpl.kt
@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getForecast(cityId: Int): ForecastResult {
        return try {
            val response = api.getForecast(cityId)

            if (response.isSuccessful) {
                ForecastResult.Success(response.body())
            } else {
                ForecastResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Unknown error"
                )
            }
        } catch (e: SocketTimeoutException) {
            ForecastResult.Error(-1, "Request timeout")
        } catch (e: UnknownHostException) {
            ForecastResult.Error(-2, "No network connection")
        } catch (e: IOException) {
            ForecastResult.Error(-3, "Network error: ${e.message}")
        } catch (e: Exception) {
            ForecastResult.Error(-4, "Unexpected error: ${e.message}")
        }
    }
}