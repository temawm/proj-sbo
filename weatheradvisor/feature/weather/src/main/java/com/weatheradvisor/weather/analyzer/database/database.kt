package com.weatheradvisor.weather.analyzer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weatheradvisor.weather.analyzer.database.entity.NotificationEntity

@Database(
    entities = [NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}