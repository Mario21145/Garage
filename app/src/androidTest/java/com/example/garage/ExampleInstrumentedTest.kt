package com.example.garage

import androidx.test.runner.AndroidJUnit4
import org.junit.runner.RunWith
import org.junit.Assert.*
import android.app.Application
import android.os.StrictMode
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.garage.database.CarDao
import com.example.garage.database.CarDatabase
import com.example.garage.models.CarDb
import com.example.garage.models.NotificationDb
import com.example.garage.viewmodels.CarViewModel
import com.example.garage.viewmodels.CarViewModelFactory
import org.junit.Before
import org.junit.Test


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var carDao: CarDao
    private lateinit var carDatabase: CarDatabase
    private lateinit var application: Application
    private lateinit var viewModel: CarViewModel

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        carDatabase = Room.inMemoryDatabaseBuilder(application, CarDatabase::class.java).build()
        carDao = carDatabase.CarDao()
        val factory = CarViewModelFactory(carDao, application)
        viewModel = ViewModelProvider(ViewModelStore(), factory)[CarViewModel::class.java]
    }

    //Cars CRUD tests
    @Test
    fun checkFetchCarData(){
        viewModel.fetchCarData()
        val result = viewModel.carLogos.value
        if (result != null) {
            assertTrue(result.isNotEmpty())
        }
    }

    @Test
    fun checkInsertGetCars(){
        val carInsertDeleteTest = CarDb(null ,"911",  "Porsche", "4200", "gasoline", "100", "this is a porsche", "2024",  "https://www.carlogos.org/car-logos/porsche-logo.png")
        viewModel.insertCar(carInsertDeleteTest)
        viewModel.getCars()
        if(viewModel.carList.value != null){
            assertTrue(viewModel.carList.value!!.isNotEmpty())
        }
    }

    @Test
    fun checkDeleteCar(){
        val carDeleteTest = CarDb(null ,"911",  "Porsche", "4200", "gasoline", "100", "this is a porsche", "2024",  "https://www.carlogos.org/car-logos/porsche-logo.png")
        viewModel.insertCar(carDeleteTest)
        viewModel.deleteCar(carDeleteTest)
        viewModel.getCars()
        if(viewModel.carList.value?.isNotEmpty() == true){
            assertTrue(viewModel.carList.value!!.isNotEmpty())
        }
    }

    @Test
    fun checkUpdateCar(){
        val carUpdateTest = CarDb(null ,"911",  "Porsche", "4200", "gasoline", "100", "this is a porsche", "2024",  "https://www.carlogos.org/car-logos/porsche-logo.png")
        viewModel.insertCar(carUpdateTest)
        val carUpdatedTest = CarDb(null ,"911",  "Porsche", "420", "gasoline", "10", "this is a porsche", "2024",  "https://www.carlogos.org/car-logos/porsche-logo.png")
        viewModel.updateCar(carUpdatedTest)
        viewModel.getCars()
        if(viewModel.carList.value?.isNotEmpty() == true){
            assertEquals(viewModel.carList.value!![0] , viewModel.updatedCar)
        }
    }

    @Test
    fun checkSetCurrentCar(){
        val carTest = CarDb(null ,"911",  "Porsche", "4200", "gasoline", "100", "this is a porsche", "2024",  "https://www.carlogos.org/car-logos/porsche-logo.png")
        viewModel.setCurrentCar(carTest)
        if(viewModel.updatedCar.value?.isNotEmpty() == true){
            assertEquals(viewModel.updatedCar.value!![0] , carTest)
        }
    }

    @Test
    fun checkClearCurrentCar(){
        val carTest = CarDb(null ,"911",  "Porsche", "4200", "gasoline", "100", "this is a porsche", "2024",  "https://www.carlogos.org/car-logos/porsche-logo.png")
        viewModel.setCurrentCar(carTest)
        viewModel.clearCurrentCar()
        if(viewModel.updatedCar.value?.isNotEmpty() == true){
            assertEquals(viewModel.updatedCar.value!![0] , "")
        }
    }


    //Notifications CRUD tests
    @Test
    fun checkInsertNotification(){
        val notificationTest = NotificationDb("911" , "To do car service")
        carDao.insertNotification(notificationTest)
        val notifications = viewModel.notifications
        if(notifications.value?.isNotEmpty() == true){
            assertTrue(notifications.value?.size!! > 0)
        }
    }

    @Test
    fun checkDeleteNotification(){
        val notificationTest = NotificationDb("911" , "To do car service")
        carDao.insertNotification(notificationTest)
        viewModel.deleteNotification(notificationTest)
        val notifications = viewModel.notifications
        if(notifications.value?.isNotEmpty() == true){
            assertTrue(notifications.value?.size!! == 0)
        }
    }

    @Test
    fun checkDeleteNotifications(){
        val notificationTest1 = NotificationDb("911" , "To do car service")
        val notificationTest2 = NotificationDb("M4" , "To do car service")
        carDao.insertNotification(notificationTest1)
        carDao.insertNotification(notificationTest2)
        carDao.deleteAllNotifications()
        val notifications = viewModel.notifications
        if(notifications.value?.isNotEmpty() == true){
            assertTrue(notifications.value?.size!! == 0)
        }
    }





}