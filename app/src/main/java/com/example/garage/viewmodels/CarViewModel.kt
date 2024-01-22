package com.example.garage.viewmodels

import android.app.Application
import android.content.Context
import android.provider.Settings.System.getString
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.garage.R
import com.example.garage.database.CarDao
import com.example.garage.models.CarDb
import com.example.garage.models.NotificationDb
import com.example.garage.models.RemoteCarData
import com.example.garage.network.CarsApi
import com.example.garage.workers.CarServiceRememberWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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

    private val _uptadedcar = MutableLiveData<List<CarDb>>()
    val updatedCar: MutableLiveData<List<CarDb>> = _uptadedcar

    private val _notifications = MutableLiveData<List<NotificationDb>>()
    val notifications: MutableLiveData<List<NotificationDb>> = _notifications


    init {
        fetchCarData()
    }



    //Logos from github repository for the cars
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

    //CRUD OPERATIONS CARS
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
        val carDb = CarDb(null ,model, brand, cubicCapacity, powerSupply, km, description , year , logo)
        carDao.insertCar(carDb)
    }

    fun getCars() {
        viewModelScope.launch(Dispatchers.Main) {
            viewModelScope.launch(Dispatchers.IO) {
                val cars = carDao.getCars()
                viewModelScope.launch(Dispatchers.Main) {
                    _carList.value = cars
                }
            }
        }
    }

    fun deleteCar(car : CarDb){
        viewModelScope.launch(Dispatchers.IO) {
            carDao.deleteCar(car)
        }
    }

    fun updateCar(car: CarDb) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                carDao.updateCar(car)
            } catch (e: Exception) {
                Log.d("CarViewModel", "Error updating car: $e")
            }
        }
    }

    fun setCurrentCar(car : CarDb){
        viewModelScope.launch(Dispatchers.Main) {
            updatedCar.value = listOf(car)
        }
    }

    fun clearCurrentCar(){
        viewModelScope.launch(Dispatchers.Main) {
            updatedCar.value = listOf()
        }
    }



    // CRUD OPERATIONS NOTIFICATIONS
    fun getNotifications() {
        viewModelScope.launch(Dispatchers.Main) {
            viewModelScope.launch(Dispatchers.IO) {
                val notifications = carDao.getNotifications()
                viewModelScope.launch(Dispatchers.Main) {
                    _notifications.value = notifications
                }
            }
        }
    }

    fun clearNotifications(){
        viewModelScope.launch(Dispatchers.IO) {
            carDao.deleteAllNotifications()
        }
    }

    fun deleteNotification(notification: NotificationDb){
        viewModelScope.launch(Dispatchers.IO) {
            carDao.deleteNotification(notification)
            carDao.getNotifications()
        }
    }

    //WORKER
    fun scheduleReminder(context: Context , currentCar : CarDb , km : String) {

            val resultKm = currentCar.km.toInt() - km.toInt()
            Log.d("State" , "Result km : $resultKm")

            if(resultKm >= 20000){

                Log.d("Data" , "${currentCar.Brand} + ${currentCar.model}")

                val inputData = Data.Builder()
                    .putString(CarServiceRememberWorker.brandKey, currentCar.Brand)
                    .putString(CarServiceRememberWorker.nameKey, currentCar.model)
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<CarServiceRememberWorker>()
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance().enqueueUniqueWork(
                    "carServiceReminderWork",
                    ExistingWorkPolicy.KEEP,
                    workRequest
                )

                val notification = NotificationDb(currentCar.model, context.getString(R.string.notificationDbTextDescription))
                carDao.insertNotification(notification)
            }
    }














    fun makeToast(context: Context, msg: String, duration: Int) {
        Toast.makeText(context, msg, duration).show()
    }
}


class CarViewModelFactory(private val carDao: CarDao, val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CarViewModel::class.java)) {
            CarViewModel(carDao, application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}