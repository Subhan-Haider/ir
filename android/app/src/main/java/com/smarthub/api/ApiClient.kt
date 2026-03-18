package com.smarthub.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://YOUR_BACKEND_IP:5000/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun esp32Service(esp32Url: String): ESP32Service {
        return Retrofit.Builder()
            .baseUrl(if(esp32Url.endsWith("/")) esp32Url else "$esp32Url/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ESP32Service::class.java)
    }
}
