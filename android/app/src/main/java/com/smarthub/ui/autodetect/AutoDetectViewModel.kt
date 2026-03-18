package com.smarthub.ui.autodetect

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smarthub.data.DeviceCode

data class AutoDetectState(
    val currentIndex: Int,
    val totalCodes: Int,
    val currentDeviceType: String,
    val currentBrand: String,
    val isFinished: Boolean,
    val foundCode: DeviceCode?
)

class AutoDetectViewModel : ViewModel() {

    private var currentFilteredCodes = listOf<DeviceCode>()
    
    val state = MutableLiveData<AutoDetectState>()
    private var currentIndex = 0
    private var selectedType = "TV"

    fun loadCodesForType(type: String, allCodes: List<DeviceCode>) {
        selectedType = type
        // The list is loaded iteratively (brand priority is assumed driven by parsing sequence)
        currentFilteredCodes = allCodes
        
        currentIndex = 0
        updateState()
    }

    private fun updateState(isFinished: Boolean = false, foundCode: DeviceCode? = null) {
        val brand = if (currentFilteredCodes.isNotEmpty() && currentIndex in currentFilteredCodes.indices) {
            currentFilteredCodes[currentIndex].brand
        } else {
            "Unknown"
        }
        
        state.value = AutoDetectState(
            currentIndex = currentIndex,
            totalCodes = currentFilteredCodes.size,
            currentDeviceType = selectedType,
            currentBrand = brand,
            isFinished = isFinished,
            foundCode = foundCode
        )
    }

    fun nextCode(step: Int = 1) {
        if (currentIndex < currentFilteredCodes.size - step) {
            currentIndex += step
            updateState()
        } else {
            currentIndex = currentFilteredCodes.size - 1
            updateState(isFinished = true)
        }
    }

    fun previousCode() {
        if (currentIndex > 0) {
            currentIndex--
            updateState()
        }
    }

    fun onResponded() {
        if (currentFilteredCodes.isNotEmpty() && currentIndex in currentFilteredCodes.indices) {
            val workingCode = currentFilteredCodes[currentIndex]
            updateState(isFinished = true, foundCode = workingCode)
        }
    }

    fun getCurrentCode(): IntArray? {
        if (currentIndex in currentFilteredCodes.indices) {
            return currentFilteredCodes[currentIndex].powerCommand
        }
        return null
    }
}
