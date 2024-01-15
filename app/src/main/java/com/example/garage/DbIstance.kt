package com.example.garage

import android.app.Application
import com.example.garage.database.CarDatabase

class DbIstance : Application() {

    val database: CarDatabase by lazy { CarDatabase.getDatabase(this) }

}