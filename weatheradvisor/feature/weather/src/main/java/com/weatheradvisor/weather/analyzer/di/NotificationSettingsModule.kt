package com.weatheradvisor.weather.analyzer.di

import android.content.Context
import android.content.SharedPreferences
import com.weatheradvisor.weather.analyzer.impl.NotificationSettingsRepositoryImpl
import com.weatheradvisor.weather.analyzer.repo.NotificationSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationSettingsModule {

    @Provides
    @Singleton
    fun provideNotificationPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideNotificationSettingsRepository(
        preferences: SharedPreferences
    ): NotificationSettingsRepository {
        return NotificationSettingsRepositoryImpl(preferences)
    }
}