package com.weatheradvisor.weather.analyzer.di

import com.weatheradvisor.weather.analyzer.model.WeatherNotificationManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WeatherNotificationManagerEntryPoint {
    fun getWeatherNotificationManager(): WeatherNotificationManager
}
