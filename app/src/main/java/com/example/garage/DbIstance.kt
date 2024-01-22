package com.example.garage

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.garage.database.CarDatabase

class DbIstance : Application() {

    val database: CarDatabase by lazy { CarDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()

        val name = "Car Reminder Channel"
        val descriptionText = "Channel for Car Reminders"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "car_reminder_id"
    }


}