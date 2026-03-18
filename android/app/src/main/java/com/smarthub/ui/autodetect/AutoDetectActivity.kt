package com.smarthub.ui.autodetect

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smarthub.data.IrDatabaseRepository
import com.smarthub.databinding.ActivityAutoDetectBinding
import com.smarthub.ir.SmartIrManager

class AutoDetectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAutoDetectBinding
    private lateinit var viewModel: AutoDetectViewModel
    private lateinit var irManager: SmartIrManager
    private var lastTransmittedIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutoDetectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irManager = SmartIrManager(this)
        viewModel = ViewModelProvider(this).get(AutoDetectViewModel::class.java)

        // Preload DB cache
        IrDatabaseRepository.loadDatabase(this)

        setupSpinner()

        viewModel.state.observe(this) { state ->
            updateUI(state)
            
            // Deterministic firing: Transmit code when index explicitly changes
            if (!state.isFinished && state.currentIndex != lastTransmittedIndex) {
                lastTransmittedIndex = state.currentIndex
                transmitCurrentCode()
            }
        }

        binding.btnTryNext.setOnClickListener {
            viewModel.nextCode(1)
        }
        
        binding.btnFastForward.setOnClickListener {
            viewModel.nextCode(5)
        }

        binding.btnRetry.setOnClickListener {
            viewModel.previousCode()
            lastTransmittedIndex = -1 
        }

        binding.btnResponded.setOnClickListener {
            viewModel.onResponded()
        }
    }

    private fun setupSpinner() {
        val types = arrayOf("TV", "AC", "Projector")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        binding.spinnerDeviceType.adapter = adapter
        
        binding.spinnerDeviceType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = types[position].lowercase()
                val targetCodes = IrDatabaseRepository.getCodesForType(type)
                lastTransmittedIndex = -1
                viewModel.loadCodesForType(types[position], targetCodes)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun transmitCurrentCode() {
        val code = viewModel.getCurrentCode()
        if (code != null) {
            // SmartIrManager auto-detects Native -> Type-C -> ESP32 priority hierarchy automatically
            irManager.transmit(38000, code)
        }
    }

    private fun updateUI(state: AutoDetectState) {
        if (state.totalCodes == 0) {
            binding.tvProgress.text = "No codes available in DB for ${state.currentDeviceType}"
            return
        }

        if (state.isFinished) {
            if (state.foundCode != null) {
                val brand = state.foundCode.brand.uppercase()
                binding.tvProgress.text = "Success! Device setup complete.\nSaved $brand code!"
                binding.progressBar.progress = 100
                binding.tvBrandPriorities.text = "Caching $brand as priority..."
                Toast.makeText(this, "IR Code securely cached locally!", Toast.LENGTH_LONG).show()
            } else {
                binding.tvProgress.text = "No working code found. End of DB."
            }
            binding.btnTryNext.isEnabled = false
            binding.btnFastForward.isEnabled = false
            binding.btnResponded.isEnabled = false
            binding.btnRetry.visibility = View.GONE
            return
        }
        
        binding.btnTryNext.isEnabled = true
        binding.btnFastForward.isEnabled = true
        binding.btnResponded.isEnabled = true

        val currentStr = "Testing code ${state.currentIndex + 1} of ${state.totalCodes}"
        binding.tvProgress.text = currentStr
        binding.tvBrandPriorities.text = "Current Brand Scope: ${state.currentBrand.uppercase()}"
        
        val progress = ((state.currentIndex + 1).toFloat() / state.totalCodes.toFloat() * 100).toInt()
        binding.progressBar.progress = progress

        binding.btnRetry.visibility = if (state.currentIndex > 0) View.VISIBLE else View.GONE
        binding.tvDeviceType.text = "Detecting ${state.currentDeviceType}..."
    }
}
