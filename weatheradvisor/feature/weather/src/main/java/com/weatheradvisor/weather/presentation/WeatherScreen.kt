package com.weatheradvisor.weather.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key.Companion.W
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weatheradvisor.weather.R
import com.weatheradvisor.weather.analyzer.models.DailySummary
import com.weatheradvisor.weather.analyzer.models.TimeSlot
import com.weatheradvisor.weather.analyzer.models.TimeSlotAnalysis
import com.weatheradvisor.weather.analyzer.models.WeatherAnalysis
import com.weatheradvisor.weather.domain.models.WeatherForecast
import com.weatheradvisor.weather.domain.usecases.WeatherForecastResult
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val screenStatus by viewModel.screenStatus
    val forecastResult by viewModel.forecast
    val weatherAnalysis by viewModel.weatherAnalysis
    val colorScheme = MaterialTheme.colorScheme
    val isSystemInDarkTheme = isSystemInDarkTheme()

    // Цвета для градиента в зависимости от темы
    val gradientColors = if (isSystemInDarkTheme) {
        listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460)
        )
    } else {
        listOf(
            Color(0xFF4A90E2),
            Color(0xFF7B68EE),
            Color(0xFF9370DB)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors)
            )
    ) {
        when (screenStatus) {
            WeatherScreenStatus.LOADING -> {
                LoadingScreen(colorScheme = colorScheme)
            }
            WeatherScreenStatus.ERROR -> {
                val errorMessage = when (val result = forecastResult) {
                    is WeatherForecastResult.Error -> result.message ?: "Неизвестная ошибка"
                    else -> "Произошла ошибка при загрузке"
                }
                ErrorScreen(
                    message = errorMessage,
                    onRetry = { viewModel.refreshWeather() },
                    colorScheme = colorScheme
                )
            }
            WeatherScreenStatus.SUCCESS -> {
                when (val result = forecastResult) {
                    is WeatherForecastResult.Success -> {
                        WeatherContent(
                            forecast = result.forecast,
                            weatherAnalysis = weatherAnalysis,
                            onCitySelect = { cityId -> viewModel.selectCity(cityId) },
                            colorScheme = colorScheme,
                            isSystemInDarkTheme = isSystemInDarkTheme,
                            viewModel = viewModel
                        )
                    }
                    is WeatherForecastResult.Error -> {
                        ErrorScreen(
                            message = result.message ?: "Неизвестная ошибка",
                            onRetry = { viewModel.refreshWeather() },
                            colorScheme = colorScheme
                        )
                    }
                    null -> {
                        ErrorScreen(
                            message = "Нет данных о погоде",
                            onRetry = { viewModel.refreshWeather() },
                            colorScheme = colorScheme
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(colorScheme: ColorScheme) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = colorScheme.onPrimary,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Загрузка погоды...",
                color = colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Ошибка",
                tint = colorScheme.onPrimary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Не удалось загрузить погоду",
                color = colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface,
                    contentColor = colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Повторить", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WeatherContent(
    forecast: WeatherForecast,
    weatherAnalysis: WeatherAnalysis?,
    onCitySelect: (Int) -> Unit,
    colorScheme: ColorScheme,
    isSystemInDarkTheme: Boolean,
    viewModel: WeatherViewModel
) {
    val currentForecast = forecast.forecasts.firstOrNull()
    var showCityDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Отступ сверху для статус бара
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Заголовок с городом и кнопкой выбора
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = forecast.cityName,
                        color = colorScheme.onPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = forecast.countryCode,
                        color = colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { showCityDialog = true },
                    modifier = Modifier
                        .background(
                            colorScheme.onPrimary.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Выбрать город",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Текущая погода
        item {
            currentForecast?.let { current ->
                CurrentWeatherCard(
                    forecast = current,
                    colorScheme = colorScheme,
                    isSystemInDarkTheme = isSystemInDarkTheme
                )
            }
        }

        // Краткая сводка дня
        weatherAnalysis?.let { analysis ->
            item {
                DailySummaryCard(
                    summary = analysis.dailySummary,
                    colorScheme = colorScheme,
                    isSystemInDarkTheme = isSystemInDarkTheme
                )
            }
        }

        // Анализ погоды по времени дня
        weatherAnalysis?.let { analysis ->
            item {
                WeatherAnalysisCard(
                    analysis = analysis,
                    colorScheme = colorScheme,
                    isSystemInDarkTheme = isSystemInDarkTheme,
                    viewModel = viewModel
                )
            }
        }

        // Дополнительная информация
        item {
            currentForecast?.let { current ->
                WeatherDetailsCard(
                    forecast = current,
                    colorScheme = colorScheme
                )
            }
        }


        // Прогноз на следующие дни
        item {
            if (forecast.forecasts.size > 1) {
                Text(
                    text = "Прогноз на сутки",
                    color = colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(forecast.forecasts.drop(1).take(6)) { dailyForecast ->
                        DailyForecastCard(
                            forecast = dailyForecast,
                            colorScheme = colorScheme,
                            isSystemInDarkTheme = isSystemInDarkTheme
                        )
                    }
                }
            }
        }

        // Отступ снизу для навигационной панели
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Диалог выбора города
    if (showCityDialog) {
        CitySelectionDialog(
            onCitySelected = { cityId ->
                onCitySelect(cityId)
                showCityDialog = false
            },
            onDismiss = { showCityDialog = false },
            colorScheme = colorScheme
        )
    }
}

@Composable
fun WeatherAnalysisCard(
    analysis: WeatherAnalysis,
    colorScheme: ColorScheme,
    isSystemInDarkTheme: Boolean,
    viewModel: WeatherViewModel
) {
    val cardColor = if (isSystemInDarkTheme) {
        colorScheme.surface.copy(alpha = 0.3f)
    } else {
        Color.White.copy(alpha = 0.15f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Анализ дня",
                color = colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf(analysis.morning, analysis.day, analysis.evening)) { timeSlot ->
                    TimeSlotCard(
                        timeSlotAnalysis = timeSlot,
                        colorScheme = colorScheme,
                        isSystemInDarkTheme = isSystemInDarkTheme,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun TimeSlotCard(
    timeSlotAnalysis: TimeSlotAnalysis,
    colorScheme: ColorScheme,
    isSystemInDarkTheme: Boolean,
    viewModel: WeatherViewModel
) {
    val timeSlotName = when (timeSlotAnalysis.timeSlot) {
        TimeSlot.MORNING -> "Утро"
        TimeSlot.DAY -> "День"
        TimeSlot.EVENING -> "Вечер"
    }

    val timeSlotIcon = when (timeSlotAnalysis.timeSlot) {
        TimeSlot.MORNING -> Icons.Default.WbSunny
        TimeSlot.DAY -> Icons.Default.WbSunny
        TimeSlot.EVENING -> Icons.Default.WbCloudy
    }

    val nestedCardColor = if (isSystemInDarkTheme) {
        colorScheme.surface.copy(alpha = 0.4f)
    } else {
        Color.White.copy(alpha = 0.25f)
    }

    Card(
        modifier = Modifier
            .width(102.dp)
            .height(viewModel.cardHeight.value.dp),
        colors = CardDefaults.cardColors(containerColor = nestedCardColor),
        shape = RoundedCornerShape(16.dp),

    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = timeSlotIcon,
                    contentDescription = timeSlotName,
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = timeSlotName,
                    color = colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${timeSlotAnalysis.avgTemperature.toInt()}°C",
                color = colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${timeSlotAnalysis.minTemperature.toInt()}°..${timeSlotAnalysis.maxTemperature.toInt()}°",
                color = colorScheme.onPrimary.copy(alpha = 0.7f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val precipitation by remember(timeSlotAnalysis) {
                derivedStateOf { timeSlotAnalysis.precipitationProbability > 30 }
            }
            val wind by remember(timeSlotAnalysis) {
                derivedStateOf { timeSlotAnalysis.windSpeed > 5 }
            }

            if (precipitation) {
                viewModel.updateCardHeightIfBigger(148)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Umbrella,
                        contentDescription = "Дождь",
                        tint = Color(0xFF42A5F5),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${timeSlotAnalysis.precipitationProbability.toInt()}%",
                        color = colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }

            if (wind) {
                viewModel.updateCardHeightIfBigger(148)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = "Ветер",
                        tint = Color(0xFFE3F2FD),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${timeSlotAnalysis.windSpeed.toInt()} м/с",
                        color = colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
            if (wind && precipitation) {
                viewModel.updateCardHeightIfBigger(168)
            }
        }
    }
}

@Composable
fun DailySummaryCard(
    summary: DailySummary,
    colorScheme: ColorScheme,
    isSystemInDarkTheme: Boolean
) {
    val cardColor = if (isSystemInDarkTheme) {
        colorScheme.surface.copy(alpha = 0.3f)
    } else {
        Color.White.copy(alpha = 0.15f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Сводка дня",
                color = colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Температурный диапазон
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Температура",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Температура: ${summary.temperatureRange.min.toInt()}° - ${summary.temperatureRange.max.toInt()}°C",
                        color = colorScheme.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Перепад: ${summary.temperatureRange.difference.toInt()}°C",
                        color = colorScheme.onPrimary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Предупреждения и рекомендации
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (summary.precipitationExpected) {
                    RecommendationItem(
                        icon = ImageVector.vectorResource(id = R.drawable.baseline_water_drop_24),
                        text = "Ожидаются осадки - возьмите зонт",
                        color = Color(0xFF4FC3F7),
                        colorScheme = colorScheme
                    )
                }

                if (summary.windyConditions) {
                    RecommendationItem(
                        icon = Icons.Default.Air,
                        text = "Ветреная погода - одевайтесь теплее",
                        color = Color(0xFFE3F2FD),
                        colorScheme = colorScheme
                    )
                }

                if (summary.temperatureFluctuations) {
                    RecommendationItem(
                        icon = Icons.Default.Warning,
                        text = "Большие перепады температуры - одевайтесь слоями",
                        color = Color(0xFFFF9800),
                        colorScheme = colorScheme
                    )
                }

                if (!summary.precipitationExpected && !summary.windyConditions && !summary.temperatureFluctuations) {
                    RecommendationItem(
                        icon = Icons.Default.CheckCircle,
                        text = "Хорошая погода для прогулок",
                        color = Color(0xFF4CAF50),
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationItem(
    icon: ImageVector,
    text: String,
    color: Color,
    colorScheme: ColorScheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = colorScheme.onPrimary.copy(alpha = 0.9f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun CurrentWeatherCard(
    forecast: WeatherForecast.DailyForecast,
    colorScheme: ColorScheme,
    isSystemInDarkTheme: Boolean
) {
    val cardColor = if (isSystemInDarkTheme) {
        colorScheme.surface.copy(alpha = 0.3f)
    } else {
        Color.White.copy(alpha = 0.15f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(vertical = 36.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Иконка погоды
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        colorScheme.onPrimary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getWeatherIcon(forecast.condition.iconCode),
                    contentDescription = forecast.condition.description,
                    tint = getWeatherIconColor(forecast.condition.iconCode),
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column {

                // Температура в цельсиях
                Text(
                    text = "${forecast.temperature.toInt()}°C",
                    color = colorScheme.onPrimary,
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Light
                )

                // Описание погоды
                Text(
                    modifier = Modifier.width(120.dp),
                    text = forecast.condition.description,
                    color = colorScheme.onPrimary.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                // Время
                Text(
                    text = SimpleDateFormat("EEEE, dd MMMM", Locale("ru"))
                        .format(Date(forecast.timestamp * 1000)),
                    color = colorScheme.onPrimary.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun WeatherDetailsCard(
    forecast: WeatherForecast.DailyForecast,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Подробности",
                color = colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDetailItem(
                    icon = Icons.Default.Water,
                    label = "Влажность",
                    value = "${forecast.humidity}%",
                    colorScheme = colorScheme
                )
                WeatherDetailItem(
                    icon = Icons.Default.Speed,
                    label = "Давление",
                    value = "${forecast.pressure} hPa",
                    colorScheme = colorScheme
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDetailItem(
                    icon = Icons.Default.Air,
                    label = "Ветер",
                    value = "${forecast.windSpeed} м/с",
                    colorScheme = colorScheme
                )
                WeatherDetailItem(
                    icon = Icons.Default.Navigation,
                    label = "Направление",
                    value = getWindDirection(forecast.windDirection),
                    colorScheme = colorScheme
                )
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    colorScheme: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colorScheme.onPrimary.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = colorScheme.onPrimary.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
@Composable
fun DailyForecastCard(
    forecast: WeatherForecast.DailyForecast,
    colorScheme: ColorScheme,
    isSystemInDarkTheme: Boolean
) {
    val cardColor = if (isSystemInDarkTheme) {
        colorScheme.surface.copy(alpha = 0.3f)
    } else {
        Color.White.copy(alpha = 0.15f)
    }

    // Преобразуем timestamp в дату и форматируем день и время
    val date = remember(forecast.timestamp) { Date(forecast.timestamp * 1000) }
    val dayText = remember(date) {
        SimpleDateFormat("EEE", Locale.getDefault()).format(date)
    }
    val timeText = remember(date) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    Card(
        modifier = Modifier.width(64.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayText,
                color = colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = timeText,
                color = colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                imageVector = getWeatherIcon(forecast.condition.iconCode),
                contentDescription = forecast.condition.description,
                tint = getWeatherIconColor(forecast.condition.iconCode),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${forecast.temperature.toInt()}°C",
                color = colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CitySelectionDialog(
    onCitySelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    colorScheme: ColorScheme
) {
    val cities = listOf(
        CityItem(524901,  "Москва",           "RU"), // Moscow
        CityItem(498817,  "Санкт-Петербург",  "RU"), // Saint Petersburg
        CityItem(1496747, "Новосибирск",      "RU"), // Novosibirsk
        CityItem(1486209, "Екатеринбург",     "RU"), // Ekaterinburg
        CityItem(551487,  "Казань",           "RU"), // Kazan
        CityItem(499068,  "Самара",           "RU"), // Samara
        CityItem(1508291, "Челябинск",        "RU"), // Chelyabinsk
        CityItem(1496153, "Омск",             "RU"), // Omsk
        CityItem(503764,  "Ростов-на-Дону",   "RU"), // Rostov-on-Don
        CityItem(479561,  "Уфа",              "RU"), // Ufa
        CityItem(1504412, "Красноярск",       "RU"), // Krasnoyarsk
        CityItem(1485490, "Пермь",            "RU"), // Perm
        CityItem(472757,  "Волгоград",        "RU"), // Volgograd
        CityItem(542420,  "Краснодар",        "RU")  // Krasnodar
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Выберите город",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp)
            ) {
                items(cities) { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCitySelected(city.id) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = city.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = city.countryCode,
                                fontSize = 14.sp,
                                color = colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = colorScheme.primary)
            }
        },
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

// Функция для получения иконки по коду погоды
@Composable
fun getWeatherIcon(iconCode: String): ImageVector {
    return when (iconCode) {
        "01d", "01n" -> Icons.Default.WbSunny
        "02d", "02n" -> Icons.Default.WbCloudy
        "03d", "03n", "04d", "04n" -> Icons.Default.Cloud
        "09d", "09n" -> Icons.Default.Grain
        "10d", "10n" -> ImageVector.vectorResource(id = R.drawable.baseline_water_drop_24)
        "11d", "11n" -> Icons.Default.Thunderstorm
        "13d", "13n" -> Icons.Default.AcUnit
        "50d", "50n" -> ImageVector.vectorResource(id = R.drawable.baseline_foggy_24)
        else -> Icons.Default.WbSunny
    }
}

// Функция для получения цвета иконки погоды
@Composable
fun getWeatherIconColor(iconCode: String): Color {
    return when (iconCode) {
        "01d", "01n" -> Color(0xFFFFD700) // Золотой для солнца
        "02d", "02n" -> Color(0xFFFFD700) // Золотой для малооблачно
        "03d", "03n", "04d", "04n" -> Color(0xFFB0BEC5) // Серый для облаков
        "09d", "09n" -> Color(0xFF4FC3F7) // Синий для дождя
        "10d", "10n" -> Color(0xFF4FC3F7) // Синий для дождя
        "11d", "11n" -> Color(0xFF9C27B0) // Фиолетовый для грозы
        "13d", "13n" -> Color(0xFFE3F2FD) // Светло-голубой для снега
        "50d", "50n" -> Color(0xFF90A4AE) // Серый для тумана
        else -> Color(0xFFFFD700)
    }
}

// Функция для преобразования градусов в направление ветра
fun getWindDirection(degrees: Int): String {
    return when (degrees) {
        in 0..22 -> "Север"
        in 23..67 -> "СВ"
        in 68..112 -> "Восток"
        in 113..157 -> "ЮВ"
        in 158..202 -> "Юг"
        in 203..247 -> "ЮЗ"
        in 248..292 -> "Запад"
        in 293..337 -> "СЗ"
        in 338..360 -> "Север"
        else -> "Н/Д"
    }
}

data class CityItem(
    val id: Int,
    val name: String,
    val countryCode: String
)