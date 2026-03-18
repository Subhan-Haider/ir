package com.smarthub.ui.debug

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smarthub.databinding.ActivityDebugRemoteBinding
import com.smarthub.ir.SmartIrManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DebugRemoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebugRemoteBinding
    private lateinit var irManager: SmartIrManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugRemoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irManager = SmartIrManager(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnTestIr.setOnClickListener {
            fireBlaster()
        }

        binding.btnAnalyze.setOnClickListener {
            analyzePayload()
        }
    }

    private fun fireBlaster() {
        try {
            val freqStr = binding.etFrequency.text.toString()
            val freq = if (freqStr.isNotEmpty()) freqStr.toInt() else 38000
            
            val payload = binding.etIrCode.text.toString().trim()
            if (payload.isEmpty()) {
                logToConsole("ERR: No payload to fire.")
                return
            }

            if (binding.rbProntoHex.isChecked) {
                logToConsole("WARN: Pronto HEX parser not yet written, use RAW arrays.")
                return
            }

            val timingsArray = parseRawTimings(payload)
            if (timingsArray.isNotEmpty()) {
                irManager.transmit(freq, timingsArray)
                logToConsole("SUCCESS: Dispatched ${timingsArray.size} pulses at ${freq}Hz")
                Toast.makeText(this, "Signal Dispatched!", Toast.LENGTH_SHORT).show()
            } else {
                logToConsole("ERR: Parsing failed. Ensure data is comma separated integers.")
            }

        } catch (e: Exception) {
            logToConsole("FATAL EXCEPTION: ${e.message}")
        }
    }

    private fun analyzePayload() {
        val payload = binding.etIrCode.text.toString().trim()
        if (payload.isEmpty()) {
            logToConsole("ANALYSIS: Payload is empty.")
            return
        }
        
        try {
            val timingsArray = parseRawTimings(payload)
            var totalDuration = 0
            for (t in timingsArray) totalDuration += t

            logToConsole("--- ANALYSIS COMPLETE ---")
            logToConsole("Total Datapoints: ${timingsArray.size}")
            logToConsole("Estimated Duration: ~${totalDuration / 1000} ms")
            logToConsole("Bit Frame Likely: ${if(timingsArray.size > 50) "YES (Contains Data Payload)" else "NO (Could be a Ping)"}")

        } catch (e: Exception) {
            logToConsole("ANALYSIS ERR: Malformed data -> ${e.message}")
        }
    }

    private fun parseRawTimings(input: String): IntArray {
        // Removes anything that isn't a digit or comma
        val cleanedInput = input.replace(Regex("[^0-9,]"), "")
        val stringArr = cleanedInput.split(",")
        val intList = mutableListOf<Int>()
        for (s in stringArr) {
            if (s.isNotBlank()) {
                intList.add(s.toInt())
            }
        }
        return intList.toIntArray()
    }

    private fun logToConsole(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val currentText = binding.tvConsoleOutput.text.toString()
        val newText = "[$time] > $message\n" + currentText
        binding.tvConsoleOutput.text = newText
    }
}
