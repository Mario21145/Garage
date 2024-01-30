package com.example.garage.models

import android.text.Editable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cars")
data class CarDb(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "model") val model: String,
    @ColumnInfo(name = "brand") val Brand: String,
    @ColumnInfo(name = "cubicCapacity") val cubicCapacity: String,
    @ColumnInfo(name = "powerSupply") val powerSupply: String,
    @ColumnInfo(name = "km") val km: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "year") val year: String,
    @ColumnInfo(name = "logo") val logo: String,
    @ColumnInfo(name = "imageLogo", typeAffinity = ColumnInfo.BLOB) val imageLogo: ByteArray?
)
