package com.smarthub.ui.tv

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smarthub.databinding.ActivityTvRemoteBinding
import com.smarthub.ir.SmartIrManager
import kotlinx.coroutines.*

class TvRemoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTvRemoteBinding
    private lateinit var irManager: SmartIrManager
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    // Mock Samsung TV arrays
    private val CODE_POWER = intArrayOf(9000, 4500, 560, 1690)
    private val CODE_VOL_UP = intArrayOf(9000, 4500, 560, 560, 1690)
    private val CODE_MUTE = intArrayOf(9000, 4500, 1690, 1690, 560)
    private val CODE_HDMI3 = intArrayOf(9000, 4500, 560, 1690, 1690)
    private val CODE_GAME_MODE = intArrayOf(8000, 4000, 600, 600)
    private val CODE_CH_5 = intArrayOf(9000, 4500, 560, 560, 560, 1690)
    private val CODE_SMART_HUB = intArrayOf(9000, 4500, 1690, 560, 1690)
    private val CODE_NAV_UP = intArrayOf(9000, 4500, 560, 560, 1690, 560)
    private val CODE_NAV_OK = intArrayOf(9000, 4500, 1690, 560, 560, 560)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvRemoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irManager = SmartIrManager(this)

        setupBasicControls()
        setupNavControls()
        setupMacros()
    }

    private fun setupBasicControls() {
        binding.btnPower.setOnClickListener { sendCommand(CODE_POWER, "Power Toggled") }
        binding.btnSource.setOnClickListener { sendCommand(CODE_HDMI3, "Switching Input") }
        binding.btnMute.setOnClickListener { sendCommand(CODE_MUTE, "Mute Toggled") }
        
        binding.btnVolUp.setOnClickListener { sendCommand(CODE_VOL_UP, "Vol +") }
        binding.btnVolDown.setOnClickListener { /* mapping here */ }
        binding.btnChUp.setOnClickListener { /* mapping here */ }
        binding.btnChDown.setOnClickListener { /* mapping here */ }
    }

    private fun setupNavControls() {
        binding.btnNavUp.setOnClickListener { sendCommand(CODE_NAV_UP, "Nav Up") }
        binding.btnNavDown.setOnClickListener { /* mapping here */ }
        binding.btnNavLeft.setOnClickListener { /* mapping here */ }
        binding.btnNavRight.setOnClickListener { /* mapping here */ }
        binding.btnNavOK.setOnClickListener { sendCommand(CODE_NAV_OK, "OK/Enter") }
    }

    private fun setupMacros() {
        // Gaming Console sequence
        binding.btnMacroGaming.setOnClickListener {
            Toast.makeText(this, "Firing Console Macro...", Toast.LENGTH_SHORT).show()
            executeMacro(
                listOf(
                    Pair(CODE_POWER, 0L),
                    Pair(CODE_HDMI3, 3000L),      // Wait for OS spin up
                    Pair(CODE_GAME_MODE, 500L)    // Set Display processor to low latency
                )
            )
        }

        binding.btnMacroNews.setOnClickListener {
            Toast.makeText(this, "Morning News Protocol Selected", Toast.LENGTH_SHORT).show()
            executeMacro(
                listOf(
                    Pair(CODE_POWER, 0L),
                    Pair(CODE_CH_5, 4000L),       // Switch to CH 5 after 4 seconds
                    Pair(CODE_MUTE, 1000L)        // Immediately Mute until you're ready
                )
            )
        }

        binding.btnMacroKids.setOnClickListener {
            Toast.makeText(this, "Loading Smart TV Kids App...", Toast.LENGTH_SHORT).show()
            executeMacro(
                listOf(
                    Pair(CODE_POWER, 0L),
                    Pair(CODE_SMART_HUB, 3000L), // Load Smart OS
                    Pair(CODE_NAV_UP, 1000L),    // Navigate OS to app tile
                    Pair(CODE_NAV_OK, 500L)      // Launch App
                )
            )
        }
    }

    private fun sendCommand(code: IntArray, toastMsg: String = "") {
        irManager.transmit(38000, code)
        if(toastMsg.isNotEmpty()) {
            Toast.makeText(this, "IR Sent: $toastMsg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executeMacro(sequence: List<Pair<IntArray, Long>>) {
        scope.launch {
            for (step in sequence) {
                if (step.second > 0) {
                    delay(step.second)
                }
                irManager.transmit(38000, step.first)
            }
            Toast.makeText(this@TvRemoteActivity, "Macro Complete!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
