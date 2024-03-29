package com.example.garage.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.slidingpanelayout.widget.SlidingPaneLayout
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

class CarViewModel(private val carDao: CarDao) : ViewModel() {

    enum class CarsApiStatus { DONE, ERROR, DEFAULT }

    private var _statusRequest = MutableLiveData<CarsApiStatus>()
    val statusRequest: LiveData<CarsApiStatus> = _statusRequest

    private val _carLogos = MutableLiveData<List<RemoteCarData>>()
    val carLogos: MutableLiveData<List<RemoteCarData>> = _carLogos

    val carList: LiveData<List<CarDb>> = carDao.getCars().asLiveData()

    private val _uptadedcar = MutableLiveData<List<CarDb>>()
    val updatedCar: MutableLiveData<List<CarDb>> = _uptadedcar

    private val _isInternetAvailable = MutableLiveData<Boolean>()
    val isInternetAvailable: LiveData<Boolean> = _isInternetAvailable

    val notifications: LiveData<List<NotificationDb>> = carDao.getNotifications().asLiveData()

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
    fun insertCar(car : CarDb) {
        carDao.insertCar(car)
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

    fun isInternetAvailable(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val activeNetwork = connectivityManager.getNetworkCapabilities(network)

        if (activeNetwork != null) {
            val isAvailable = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
            _isInternetAvailable.postValue(isAvailable)
        } else {
            _isInternetAvailable.postValue(false)
        }
    }

    fun urlToByteArray(imageUrl: String): ByteArray {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()

            val input: InputStream = connection.getInputStream()
            val output = ByteArrayOutputStream()

            val buffer = ByteArray(4096)
            var bytesRead: Int

            try {
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            } finally {
                input.close()
            }
        return output.toByteArray()
    }


    fun uriToByteArray(context: Context, uri: Uri): ByteArray {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val buffer = ByteArrayOutputStream()

            inputStream?.use { input ->
                val bufferSize = 4096
                val byteArray = ByteArray(bufferSize)
                var bytesRead: Int

                while (input.read(byteArray, 0, bufferSize).also { bytesRead = it } != -1) {
                    buffer.write(byteArray, 0, bytesRead)
                }
            }
            return buffer.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            return ByteArray(0)
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
            CarViewModel(carDao) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}