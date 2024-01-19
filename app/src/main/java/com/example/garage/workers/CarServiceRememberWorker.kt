package com.example.garage.workers

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.garage.MainActivity
import com.example.garage.R
import com.example.garage.repository.Preferences

class CarServiceRememberWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent : PendingIntent = PendingIntent.getActivity(applicationContext , 0 , intent, PendingIntent.FLAG_IMMUTABLE)
        val carName = inputData.getString(nameKey)
        val builder = NotificationCompat.Builder(applicationContext, "Notification")
            .setSmallIcon(R.drawable.fuel)
            .setContentTitle("Check me!")
            .setContentText("It's time to check $carName car")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = Preferences.getNotificationIdCounter(applicationContext)

        val NOTIFICATION_PERMISSION_REQUEST_CODE = 156

        with(NotificationManagerCompat.from(applicationContext)) {

            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    applicationContext as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }

            notify(notificationId, builder.build())
        }

        Preferences.incrementAndSaveNotificationIdCounter(applicationContext)
        return Result.success()
    }

    companion object {
        val nameKey = "carServiceWorker"
    }

}