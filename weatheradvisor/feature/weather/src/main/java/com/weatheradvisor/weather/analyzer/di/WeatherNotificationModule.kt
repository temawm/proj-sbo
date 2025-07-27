package com.weatheradvisor.weather.analyzer.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.weatheradvisor.weather.analyzer.database.NotificationDao
import com.weatheradvisor.weather.analyzer.impl.NotificationRepositoryImpl
import com.weatheradvisor.weather.analyzer.model.WeatherAnalyzer
import com.weatheradvisor.weather.analyzer.model.WeatherNotificationManager
import com.weatheradvisor.weather.analyzer.model.WeatherRecommendationGenerator
import com.weatheradvisor.weather.analyzer.presentation.WeatherNotificationService
import com.weatheradvisor.weather.analyzer.repo.NotificationRepository
import com.weatheradvisor.weather.domain.usecases.GetWeatherForecastUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherNotificationModule {

    @Provides
    @Singleton
    fun provideWeatherAnalyzer(): WeatherAnalyzer {
        return WeatherAnalyzer()
    }

    @Provides
    @Singleton
    fun provideWeatherRecommendationGenerator(): WeatherRecommendationGenerator {
        return WeatherRecommendationGenerator()
    }

    @Provides
    @Singleton
    fun provideWeatherNotificationManager(
        @ApplicationContext context: Context,
        notificationRepository: NotificationRepository
    ): WeatherNotificationManager {
        return WeatherNotificationManager(context, notificationRepository)
    }

    @Provides
    @Singleton
    fun provideWeatherNotificationService(
        weatherAnalyzer: WeatherAnalyzer,
        recommendationGenerator: WeatherRecommendationGenerator,
        notificationManager: WeatherNotificationManager,
        getWeatherUseCase: GetWeatherForecastUseCase
    ): WeatherNotificationService {
        return WeatherNotificationService(
            weatherAnalyzer,
            recommendationGenerator,
            notificationManager,
            getWeatherUseCase
        )
    }

    @Provides
    @Singleton
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        dao: NotificationDao
    ): NotificationRepository = NotificationRepositoryImpl(dao)

}