package com.example.garage.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garage.database.CarDao
import com.example.garage.models.CarDb
import com.example.garage.models.RemoteCarData
import com.example.garage.network.CarsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CarViewModel(dao: CarDao) : ViewModel() {

    enum class CarsApiStatus { DONE, ERROR, DEFAULT }

    private var _statusRequest = MutableLiveData<CarsApiStatus>().apply {
        value = CarsApiStatus.DEFAULT
    }
    val statusRequest: LiveData<CarsApiStatus> = _statusRequest

    private val _carList = MutableLiveData<List<RemoteCarData>>()
    val carList: MutableLiveData<List<RemoteCarData>> = _carList

    val carDao = dao

    init {
        fetchCarData()
    }

    fun fetchCarData() {
        viewModelScope.launch(Dispatchers.Main) {
            while (_statusRequest.value != CarsApiStatus.DONE) {
                try {
                    _carList.value = CarsApi.retrofitService.getCarList().cars
                    if (_carList.value!!.isNotEmpty()) {
                        _statusRequest.value = CarsApiStatus.DONE
                    }
                } catch (e: Exception) {
                    _statusRequest.value = CarsApiStatus.ERROR
                    _carList.value = listOf()
                }
            }
        }
    }

    //CRUD OPERATIONS
    fun insertCar(
        name: String,
        cubicCapacity: String,
        powerSupply: String,
        model: String,
        km: Int
    ) {
        val carDb = CarDb(
            name = name,
            cubicCapacity = cubicCapacity,
            powerSupply = powerSupply,
            model = model,
            km = km
        )
        carDao.insertCar(carDb)
    }


}