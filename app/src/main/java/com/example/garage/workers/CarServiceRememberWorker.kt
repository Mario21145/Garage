package com.example.garage.workers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings.System.getString
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.garage.MainActivity
import com.example.garage.R
import com.example.garage.repository.Preferences

class CarServiceRememberWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent : PendingIntent = PendingIntent.getActivity(applicationContext , 0 , intent, PendingIntent.FLAG_IMMUTABLE)
        val carName = inputData.getString(nameKey)
        val carBrand = inputData.getString(brandKey)
        val title = applicationContext.getString(R.string.notificationTextTitle)
        val description = applicationContext.getString(R.string.notificationTextDescription , carBrand , carName)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.engine)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = Preferences.getNotificationIdCounter(applicationContext)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, builder.build())
        }

        Preferences.incrementAndSaveNotificationIdCounter(applicationContext)

        return Result.success()
    }

    companion object {
        val nameKey = "carServiceWorker"
        val brandKey = "carServiceBrand"
        val CHANNEL_ID = "car_reminder_id"
    }

}