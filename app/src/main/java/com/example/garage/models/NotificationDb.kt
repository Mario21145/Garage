package com.example.garage.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notifications")
data class NotificationDb(
    @PrimaryKey @ColumnInfo(name = "carName") val carName: String,
    @ColumnInfo(name = "notificationDescription") val description: String
)
