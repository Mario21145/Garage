package com.example.garage.viewmodels

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.garage.database.CarDao
import com.example.garage.models.CarDb
import com.example.garage.models.RemoteCarData
import com.example.garage.network.CarsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarViewModel(private val carDao: CarDao, application: Application) : ViewModel() {

    enum class CarsApiStatus { DONE, ERROR, DEFAULT }

    private var _statusRequest = MutableLiveData<CarsApiStatus>().apply {
        value = CarsApiStatus.DEFAULT
    }
    val statusRequest: LiveData<CarsApiStatus> = _statusRequest

    private val _carLogos = MutableLiveData<List<RemoteCarData>>()
    val carLogos: MutableLiveData<List<RemoteCarData>> = _carLogos

    private val _carList = MutableLiveData<List<CarDb>>()
    val carList: MutableLiveData<List<CarDb>> = _carList

    private val _currentCar = mutableListOf<CarDb>()
    val currentCar = _currentCar

    init {
        fetchCarData()
    }

    fun fetchCarData() {
        viewModelScope.launch(Dispatchers.Main) {
            while (_statusRequest.value != CarsApiStatus.DONE) {
                try {
                    _carLogos.value = CarsApi.retrofitService.getCarList().cars
                    if (_carLogos.value!!.isNotEmpty()) {
                        _statusRequest.value = CarsApiStatus.DONE
                    }
                } catch (e: Exception) {
                    _statusRequest.value = CarsApiStatus.ERROR
                    _carLogos.value = listOf()
                }
            }
        }
    }

    //CRUD OPERATIONS
    fun insertCar(
        model: String,
        brand: String,
        cubicCapacity: String,
        powerSupply: String,
        km: String,
        description: String,
        year : String,
        logo: String,
    ) {
        val carDb = CarDb(model, brand, cubicCapacity, powerSupply, km, description , year , logo)
        carDao.insertCar(carDb)
    }

    fun getCars() {
        viewModelScope.launch(Dispatchers.Main) {
            viewModelScope.launch(Dispatchers.IO) {
                val cars = carDao.getCars()
                viewModelScope.launch(Dispatchers.Main) {
                    _carList.value = cars
                    Log.d("Data" , "Data db: ${_carList.value}")
                }
            }
        }
    }

    fun setCurrentCar(car : CarDb){
        _currentCar.add(car)
    }

    fun clearCurrentCar(){
        currentCar.clear()
    }

}


class CarViewModelFactory(private val carDao: CarDao, private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CarViewModel::class.java)) {
            CarViewModel(carDao, application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}