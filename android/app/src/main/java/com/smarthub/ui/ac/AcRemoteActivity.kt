package com.smarthub.ui.ac

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smarthub.databinding.ActivityAcRemoteBinding
import com.smarthub.ir.SmartIrManager

class AcRemoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcRemoteBinding
    private lateinit var irManager: SmartIrManager

    // Typical AC state tracked by remote
    private var currentTemp = 24
    private var isPowerOn = false
    private val modes = arrayOf("❄️ COOL", "💧 DRY", "🌀 FAN", "☀️ HEAT", "🔄 AUTO")
    private var currentModeIndex = 0
    private val fanSpeeds = arrayOf("LOW", "MED", "HIGH", "AUTO")
    private var currentFanIndex = 3 // AUTO

    // Dummy code array for demonstration matching DaiKin / generic AC arrays
    // AC IR protocols are usually very long single states: [Power, Mode, Temp, Fan] all sent together.
    private val CODE_UPDATE_STATE = intArrayOf(4000, 1000, 200, 800, 200, 800) 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcRemoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irManager = SmartIrManager(this)

        updateDisplay()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnPower.setOnClickListener {
            isPowerOn = !isPowerOn
            transmitState("Power ${if (isPowerOn) "ON" else "OFF"}")
            updateDisplay()
        }

        binding.btnTempUp.setOnClickListener {
            if (currentTemp < 30) {
                currentTemp++
                transmitState("Temp: $currentTemp°C")
                updateDisplay()
            }
        }

        binding.btnTempDown.setOnClickListener {
            if (currentTemp > 16) {
                currentTemp--
                transmitState("Temp: $currentTemp°C")
                updateDisplay()
            }
        }

        binding.btnMode.setOnClickListener {
            currentModeIndex = (currentModeIndex + 1) % modes.size
            transmitState("Mode: ${modes[currentModeIndex]}")
            updateDisplay()
        }

        binding.btnFan.setOnClickListener {
            currentFanIndex = (currentFanIndex + 1) % fanSpeeds.size
            transmitState("Fan: ${fanSpeeds[currentFanIndex]}")
            updateDisplay()
        }

        binding.btnSwing.setOnClickListener {
            transmitState("Swing Toggled")
        }

        // Smart Manual Scene Macros
        binding.btnMacroTurbo.setOnClickListener {
            currentTemp = 18
            currentModeIndex = 0 // COOL
            currentFanIndex = 2  // HIGH
            isPowerOn = true
            transmitState("⚡ Turbo Mode Activated!")
            updateDisplay()
        }

        binding.btnMacroBedtime.setOnClickListener {
            currentTemp = 25
            currentModeIndex = 0 // COOL
            currentFanIndex = 0  // LOW (Quiet)
            isPowerOn = true
            transmitState("🌙 Bedtime Mode Activated")
            updateDisplay()
        }

        binding.btnMacroEco.setOnClickListener {
            currentTemp = 26
            currentModeIndex = 4 // AUTO
            currentFanIndex = 3  // AUTO
            isPowerOn = true
            transmitState("🌱 Eco Mode Activated")
            updateDisplay()
        }
    }

    private fun transmitState(toastMsg: String) {
        // In reality, AC remotes construct a full byte array encompassing all states: Temp, Mode, Fan.
        // For blueprint, we simulate the state construction.
        irManager.transmit(38000, CODE_UPDATE_STATE)
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show()
    }

    private fun updateDisplay() {
        binding.tvTempDisplay.text = if (isPowerOn) "${currentTemp}°C" else "--°C"
        binding.tvModeDisplay.text = if (isPowerOn) modes[currentModeIndex] else ""
        binding.tvFanDisplay.text = if (isPowerOn) "💨 ${fanSpeeds[currentFanIndex]}" else ""
        
        binding.tvTempDisplay.setTextColor(if (isPowerOn) android.graphics.Color.parseColor("#00E5FF") else android.graphics.Color.GRAY)
    }
}
