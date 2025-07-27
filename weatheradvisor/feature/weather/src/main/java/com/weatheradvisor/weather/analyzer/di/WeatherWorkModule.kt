package com.weatheradvisor.weather.analyzer.di

import android.content.Context
import androidx.work.WorkManager
import com.weatheradvisor.weather.analyzer.sheduler.WeatherWorkScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherWorkModule {

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWeatherWorkScheduler(
        workManager: WorkManager
    ): WeatherWorkScheduler {
        return WeatherWorkScheduler(workManager)
    }
}