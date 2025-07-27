package com.weatheradvisor.weather.analyzer.di

import android.content.Context
import androidx.room.Room
import com.weatheradvisor.weather.analyzer.database.NotificationDao
import com.weatheradvisor.weather.analyzer.database.NotificationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNotificationDatabase(
        @ApplicationContext context: Context
    ): NotificationDatabase {
        return Room.databaseBuilder(
            context,
            NotificationDatabase::class.java,
            "notifications.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(
        database: NotificationDatabase
    ): NotificationDao = database.notificationDao()
}