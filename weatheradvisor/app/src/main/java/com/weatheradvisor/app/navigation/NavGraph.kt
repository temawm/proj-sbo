package com.weatheradvisor.app.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weatheradvisor.weather.presentation.WeatherScreen
import com.weatheradvisor.weather.presentation.WeatherViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "weather_screen") {
        composable ("weather_screen") {
            val viewModel: WeatherViewModel = hiltViewModel()
            WeatherScreen(viewModel)
        }
    }
}