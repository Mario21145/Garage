package com.example.garage.repository

import android.content.Context

object Preferences{

    private const val PREFS_FILE_NAME = "my_app_prefs"
    private const val NOTIFICATION_COUNTER_KEY = "notification_counter_key"

    private fun getSharedPreferences(context: Context) = context.getSharedPreferences(
        PREFS_FILE_NAME, Context.MODE_PRIVATE)

    fun getNotificationIdCounter(context: Context): Int {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getInt(NOTIFICATION_COUNTER_KEY, 0)
    }

    fun incrementAndSaveNotificationIdCounter(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val currentCounter = getNotificationIdCounter(context)
        val newCounter = currentCounter + 1
        sharedPreferences.edit().putInt(NOTIFICATION_COUNTER_KEY, newCounter).apply()
    }

}