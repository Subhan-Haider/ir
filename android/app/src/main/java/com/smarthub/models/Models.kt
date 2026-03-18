package com.smarthub.models

data class Device(
    val _id: String,
    val name: String,
    val deviceType: String,
    val brand: String?
)

data class ESP32Payload(
    val protocol: String,
    val rawData: List<Int>? = null,
    val code: String? = null,
    val bits: Int? = null
)
