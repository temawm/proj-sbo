package com.weatheradvisor.weather.presentation

import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatheradvisor.network.domain.ForecastResult
import com.weatheradvisor.weather.analyzer.model.WeatherRecommendationGenerator
import com.weatheradvisor.weather.analyzer.models.RecommendationPriority
import com.weatheradvisor.weather.analyzer.models.RecommendationType
import com.weatheradvisor.weather.analyzer.models.WeatherAnalysis
import com.weatheradvisor.weather.analyzer.models.WeatherRecommendation
import com.weatheradvisor.weather.analyzer.presentation.WeatherNotificationService
import com.weatheradvisor.weather.analyzer.repo.NotificationSettingsRepository
import com.weatheradvisor.weather.domain.models.WeatherForecast
import com.weatheradvisor.weather.domain.usecases.GetWeatherForecastUseCase
import com.weatheradvisor.weather.domain.usecases.WeatherForecastResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeather: GetWeatherForecastUseCase,
    private val weatherNotificationService: WeatherNotificationService,
    private val notificationSettingsRepository: NotificationSettingsRepository
) : ViewModel() {

    val screenStatus = mutableStateOf(WeatherScreenStatus.LOADING)
    val forecast = mutableStateOf<WeatherForecastResult?>(null)
    val notificationDebugInfo = mutableStateOf("")

    val cardHeight = mutableStateOf(128)

    val weatherRecommendations = mutableStateOf<List<WeatherRecommendation>>(emptyList())
    val weatherAnalysis = mutableStateOf<WeatherAnalysis?>(null)

    private val _requestNotificationPermission = Channel<Unit>(Channel.BUFFERED)

    private var lastCityId: Int? = null

    init {
        initialization()
    }

    private fun initialization() {
        screenStatus.value = WeatherScreenStatus.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cityId = 542420
                lastCityId = cityId

                val result = getWeather(cityId)
                forecast.value = result
                screenStatus.value = WeatherScreenStatus.SUCCESS

                notificationSettingsRepository.setSelectedCityId(cityId)
                processWeatherNotifications(cityId)

            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error loading weather", e)
                screenStatus.value = WeatherScreenStatus.ERROR
            }
        }
    }

    fun updateCardHeightIfBigger(newValue: Int) {
        if (newValue > cardHeight.value) {
            cardHeight.value = newValue
        }
    }

    fun refreshWeather() {
        initialization()
    }

    fun selectCity(cityId: Int) {
        screenStatus.value = WeatherScreenStatus.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                lastCityId = cityId

                val result = getWeather(cityId)
                forecast.value = result
                screenStatus.value = WeatherScreenStatus.SUCCESS

                notificationSettingsRepository.setSelectedCityId(cityId)
                processWeatherNotifications(cityId)

            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error loading weather for city $cityId", e)
                screenStatus.value = WeatherScreenStatus.ERROR
            }
        }
    }

    private suspend fun processWeatherNotifications(cityId: Int) {
        try {
            val notificationsEnabled = notificationSettingsRepository.isNotificationsEnabled()

            val analysis = weatherNotificationService.getWeatherAnalysis(cityId)
            weatherAnalysis.value = analysis

            val recommendations = if (notificationsEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    _requestNotificationPermission.send(Unit)
                    return
                }
                weatherNotificationService.processWeatherNotifications(cityId)
            } else {
                if (analysis != null) {
                    WeatherRecommendationGenerator().generateRecommendations(analysis)
                } else {
                    emptyList()
                }
            }

            // Сохраняем рекомендации для UI
            weatherRecommendations.value = recommendations

            updateDebugInfo("Получено ${recommendations.size} рекомендаций для города: $cityId")
            Log.d("WeatherViewModel", "Recommendations: ${recommendations.map { it.message }}")

        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Error processing notifications", e)
            updateDebugInfo("Ошибка обработки уведомлений: ${e.message}")
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (granted) {
                lastCityId?.let { cityId ->
                    val recommendations = weatherNotificationService.processWeatherNotifications(cityId)
                    weatherRecommendations.value = recommendations
                    updateDebugInfo("Уведомления разрешены, запланировано ${recommendations.size} уведомлений")
                }
            } else {
                updateDebugInfo("Разрешение на уведомления не предоставлено")
            }
        }
    }


    private fun updateDebugInfo(info: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        notificationDebugInfo.value = "[$timestamp] $info"
        Log.d("WeatherViewModel", info)
    }
}

enum class WeatherScreenStatus {
    SUCCESS,
    LOADING,
    ERROR,
}
