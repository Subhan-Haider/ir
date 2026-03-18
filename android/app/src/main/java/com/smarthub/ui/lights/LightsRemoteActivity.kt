package com.smarthub.ui.lights

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smarthub.databinding.ActivityLightsRemoteBinding
import com.smarthub.ir.SmartIrManager

class LightsRemoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLightsRemoteBinding
    private lateinit var irManager: SmartIrManager

    // Dummy Light / Fan Arrays
    private val CODE_POWER = intArrayOf(9000, 4500, 560, 1690)
    private val CODE_DIM = intArrayOf(3000, 1500, 500, 1000)
    private val CODE_BRIGHT = intArrayOf(3000, 1500, 1000, 500)
    private val CODE_WARM = intArrayOf(4000, 2000, 400, 1200)
    private val CODE_COOL = intArrayOf(4000, 2000, 1200, 400)
    private val CODE_FAN_OFF = intArrayOf(5000, 2500, 600, 600)
    private val CODE_FAN_1 = intArrayOf(5000, 2500, 1200, 600)
    private val CODE_FAN_3 = intArrayOf(5000, 2500, 600, 1200)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLightsRemoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irManager = SmartIrManager(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnPowerAll.setOnClickListener { send(CODE_POWER, "Room Power Toggled") }
        
        binding.btnDim.setOnClickListener { send(CODE_DIM, "Dimming Lights") }
        binding.btnBright.setOnClickListener { send(CODE_BRIGHT, "Max Brightness") }
        
        binding.btnWarm.setOnClickListener { send(CODE_WARM, "Warm Atmosphere 3000K") }
        // Add neutral...
        binding.btnCool.setOnClickListener { send(CODE_COOL, "Cool White 6000K") }

        binding.btnFanOff.setOnClickListener { send(CODE_FAN_OFF, "Fan Motor OFF") }
        binding.btnFan1.setOnClickListener { send(CODE_FAN_1, "Fan Speed: Low") }
        binding.btnFan3.setOnClickListener { send(CODE_FAN_3, "Fan Speed: High") }
    }

    private fun send(code: IntArray, msg: String) {
        irManager.transmit(38000, code)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
