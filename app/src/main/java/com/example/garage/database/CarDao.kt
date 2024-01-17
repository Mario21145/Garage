package com.example.garage.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.garage.models.CarDb

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCar(car: CarDb)

    @Query("SELECT * FROM cars")
    fun getCars() : List<CarDb>

    @Delete
    fun deleteCar(item : CarDb)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCar(car: CarDb)


//    @Query("UPDATE PlantDb SET state = :state WHERE name = :name")
//    fun updateState(state : Boolean , name: String)


}