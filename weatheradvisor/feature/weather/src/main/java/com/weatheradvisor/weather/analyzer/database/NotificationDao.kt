package com.weatheradvisor.weather.analyzer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weatheradvisor.weather.analyzer.database.entity.NotificationEntity

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isSent = 1 WHERE id = :id")
    fun markAsSent(id: String)

    @Query("SELECT * FROM notifications WHERE isScheduled = 1 AND isSent = 0")
    fun getScheduledNotifications(): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE isSent = 1")
    fun getSentNotifications(): List<NotificationEntity>

    @Query("DELETE FROM notifications WHERE scheduledTime < :timestamp")
    fun deleteOldNotifications(timestamp: Long)
}