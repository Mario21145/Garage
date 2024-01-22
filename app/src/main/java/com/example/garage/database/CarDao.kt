package com.example.garage.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.garage.models.CarDb
import com.example.garage.models.NotificationDb

@Dao
interface CarDao {


    //Cars
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCar(car: CarDb)

    @Query("SELECT * FROM cars")
    fun getCars() : List<CarDb>

    @Query("SELECT * FROM cars WHERE model = :model")
    fun getCar(model : String) : CarDb

    @Delete
    fun deleteCar(item : CarDb)

    @Update
    fun updateCar(car: CarDb)



    //Notifications
    @Query("SELECT * FROM notifications")
    fun getNotifications() : List<NotificationDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: NotificationDb)

    @Delete
    fun deleteNotification(item : NotificationDb)

    @Query("DELETE FROM notifications")
     fun deleteAllNotifications()

}
