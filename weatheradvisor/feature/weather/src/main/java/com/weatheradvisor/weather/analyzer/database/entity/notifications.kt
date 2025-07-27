package com.weatheradvisor.weather.analyzer.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val message: String,
    val priority: String,
    val scheduledTime: Long,
    val type: String,
    val isScheduled: Boolean = false,
    val isSent: Boolean = false
)