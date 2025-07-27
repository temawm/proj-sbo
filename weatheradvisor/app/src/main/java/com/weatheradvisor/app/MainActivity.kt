package com.weatheradvisor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.weatheradvisor.app.navigation.NavGraph
import com.weatheradvisor.di.ApiKeyProvider
import com.weatheradvisor.ui.theme.WeatherAdvisorTheme
import com.weatheradvisor.weather.WeatherAdvisorColorScheme
import com.weatheradvisor.weather.presentation.WeatherScreen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiKeyProvider.apiKey = BuildConfig.OPENWEATHER_API_KEY

        enableEdgeToEdge()
        setContent {
            WeatherAdvisorColorScheme {
                NavGraph()
            }
        }
    }
}
