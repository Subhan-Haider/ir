package com.smarthub.ui.soundbar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smarthub.databinding.ActivitySoundbarRemoteBinding
import com.smarthub.ir.SmartIrManager
import kotlinx.coroutines.*

class SoundbarRemoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySoundbarRemoteBinding
    private lateinit var irManager: SmartIrManager
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    // Simulated IR Codes for a generic soundbar
    private val CODE_POWER = intArrayOf(9000, 4500, 560, 1690)
    private val CODE_VOL_UP = intArrayOf(9000, 4500, 560, 560, 1690)
    private val CODE_VOL_DOWN = intArrayOf(9000, 4500, 560, 1690, 560)
    private val CODE_MUTE = intArrayOf(9000, 4500, 1690, 1690, 560)
    private val CODE_INPUT_BLUETOOTH = intArrayOf(9000, 4500, 1690, 560, 560)
    private val CODE_INPUT_OPTICAL = intArrayOf(9000, 4500, 560, 1690, 1690)
    
    // EQ Profiles
    private val CODE_EQ_MOVIE = intArrayOf(8000, 4000, 600, 1200)
    private val CODE_EQ_MUSIC = intArrayOf(8000, 4000, 1200, 600)
    private val CODE_EQ_VOICE = intArrayOf(8000, 4000, 600, 600)
    private val CODE_EQ_NIGHT = intArrayOf(8000, 4000, 1200, 1200)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoundbarRemoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irManager = SmartIrManager(this)

        setupBasicControls()
        setupInputs()
        setupEQProfiles()
    }

    private fun setupBasicControls() {
        binding.btnPower.setOnClickListener { sendCommand(CODE_POWER, "Power Toggled") }
        binding.btnVolUp.setOnClickListener { sendCommand(CODE_VOL_UP, "Volume Up") }
        binding.btnVolDown.setOnClickListener { sendCommand(CODE_VOL_DOWN, "Volume Down") }
        binding.btnMute.setOnClickListener { sendCommand(CODE_MUTE, "Mute Toggled") }
    }

    private fun setupInputs() {
        binding.btnInputBluetooth.setOnClickListener { sendCommand(CODE_INPUT_BLUETOOTH, "Bluetooth Mode") }
        binding.btnInputOptical.setOnClickListener { sendCommand(CODE_INPUT_OPTICAL, "Optical Line") }
        // Implement others as needed
    }

    private fun setupEQProfiles() {
        binding.btnEQMovie.setOnClickListener { sendCommand(CODE_EQ_MOVIE, "Movie Surround Activated") }
        binding.btnEQMusic.setOnClickListener { sendCommand(CODE_EQ_MUSIC, "Music Flat EQ Activated") }
        binding.btnEQVoice.setOnClickListener { sendCommand(CODE_EQ_VOICE, "Clear Voice Activated") }
        binding.btnEQNight.setOnClickListener { sendCommand(CODE_EQ_NIGHT, "Night Mode Compression ON") }
    }

    private fun sendCommand(code: IntArray, toastMsg: String) {
        irManager.transmit(38000, code)
        Toast.makeText(this, "IR Sent: $toastMsg", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
