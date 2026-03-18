package com.smarthub.ui.projector

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smarthub.databinding.ActivityProjectorRemoteBinding
import com.smarthub.ir.SmartIrManager
import kotlinx.coroutines.*

class ProjectorRemoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectorRemoteBinding
    private lateinit var irManager: SmartIrManager
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    // Dummy Samsung/Epson HEX codes defined as IntArrays for the blueprint
    private val CODE_POWER = intArrayOf(9000, 4500, 560, 1690)
    private val CODE_HDMI1 = intArrayOf(9000, 4500, 560, 560)
    private val CODE_HDMI2 = intArrayOf(9000, 4500, 1690, 560)
    private val CODE_KEYSTONE_UP = intArrayOf(4000, 4000, 500, 1500)
    private val CODE_ECO_LAMP = intArrayOf(8000, 4000, 600, 600)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectorRemoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irManager = SmartIrManager(this)

        setupBasicControls()
        setupKeystoneControls()
        setupMacroButtons()
    }

    private fun setupBasicControls() {
        binding.btnPower.setOnClickListener { sendCommand(CODE_POWER, "Power Toggled") }
        binding.btnInputHDMI1.setOnClickListener { sendCommand(CODE_HDMI1, "HDMI 1 Selected") }
        binding.btnInputHDMI2.setOnClickListener { sendCommand(CODE_HDMI2, "HDMI 2 Selected") }
        
        // Single tap / Long press mappings
        binding.btnInputHDMI1.setOnLongClickListener {
            Toast.makeText(this, "Long Press: Assigning as Default Boot Source", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupKeystoneControls() {
        binding.btnKeystoneUp.setOnClickListener { sendCommand(CODE_KEYSTONE_UP, "Keystone Up") }
        binding.btnKeystoneDown.setOnClickListener { /* mapping here */ }
        binding.btnKeystoneLeft.setOnClickListener { /* mapping here */ }
        binding.btnKeystoneRight.setOnClickListener { /* mapping here */ }
        
        binding.btnZoomIn.setOnClickListener { /* mapping here */ }
        binding.btnFocusPlus.setOnClickListener { /* mapping here */ }
    }

    private fun setupMacroButtons() {
        // Presentation Mode Macro Sequence
        binding.btnMacroPresentation.setOnClickListener {
            Toast.makeText(this, "Running Presentation Macro...", Toast.LENGTH_SHORT).show()
            executeMacro(
                listOf(
                    Pair(CODE_POWER, 0L),       // Power ON
                    Pair(CODE_HDMI1, 5000L),    // Wait 5s for boot, switch to HDMI 1
                    Pair(CODE_ECO_LAMP, 1000L)  // Wait 1s, activate ECO Mode
                )
            )
        }

        // Movie Mode Macro Sequence
        binding.btnMacroMovie.setOnClickListener {
            Toast.makeText(this, "Running Movie Macro...", Toast.LENGTH_SHORT).show()
            executeMacro(
                listOf(
                    Pair(CODE_POWER, 0L),
                    Pair(CODE_HDMI2, 5000L) // Switch to HDMI2 (e.g., Apple TV)
                )
            )
        }

        // Timer Macro Example
        binding.btnMacroPowerOff.setOnClickListener {
            Toast.makeText(this, "Projector will Sleep in 30 minutes", Toast.LENGTH_LONG).show()
            // In a real app, you'd hook this to Android WorkManager for accurate background delays
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
                    delay(step.second) // Wait the specified delay
                }
                irManager.transmit(38000, step.first)
            }
            Toast.makeText(this@ProjectorRemoteActivity, "Macro Complete!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
