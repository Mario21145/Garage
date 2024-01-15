package com.example.garage.network


import com.example.garage.models.RemoteCarData
import com.example.garage.models.RemoteCarDataResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val BASE_URL = "https://raw.githubusercontent.com/Mario21145/GarageRepository/main/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface CarApiService{
    @GET("cars.json")
    suspend fun getCarList() : RemoteCarDataResponse
}

object CarsApi {
    val retrofitService: CarApiService by lazy {
        retrofit.create(CarApiService::class.java)
    }
}