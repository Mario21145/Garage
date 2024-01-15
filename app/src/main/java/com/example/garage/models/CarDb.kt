package com.example.garage.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cars")
data class CarDb(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "cubicCapacity") val cubicCapacity: String,
    @ColumnInfo(name = "powerSupply" ) val powerSupply: String,
    @ColumnInfo(name = "model" ) val model: String,
    @ColumnInfo(name = "km" ) val km: Int,
)