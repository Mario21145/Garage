package com.example.garage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.garage.models.CarDb

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCar(car: CarDb)

    @Query("SELECT * FROM carDb")
    fun getCars() : List<CarDb>




//    @Query("SELECT state FROM plantdb WHERE name = :name LIMIT 1")
//    fun getPlantState(name : String) : Boolean
//
//    @Query("UPDATE PlantDb SET state = :state WHERE name = :name")
//    fun updateState(state : Boolean , name: String)


}