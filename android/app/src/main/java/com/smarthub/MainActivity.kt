package com.smarthub

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smarthub.databinding.ActivityMainBinding
import com.smarthub.ir.SmartIrManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var irManager: SmartIrManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irManager = SmartIrManager(this)

        // Initial App Load Check
        if (!hasHardware()) {
            showConnectIrDialog()
        }

        binding.cardNavTv.setOnClickListener { navigateTo(com.smarthub.ui.tv.TvRemoteActivity::class.java) }
        binding.cardNavAc.setOnClickListener { navigateTo(com.smarthub.ui.ac.AcRemoteActivity::class.java) }
        binding.cardNavProjector.setOnClickListener { navigateTo(com.smarthub.ui.projector.ProjectorRemoteActivity::class.java) }
        binding.cardNavSoundbar.setOnClickListener { navigateTo(com.smarthub.ui.soundbar.SoundbarRemoteActivity::class.java) }
        binding.cardNavLights.setOnClickListener { navigateTo(com.smarthub.ui.lights.LightsRemoteActivity::class.java) }
        binding.cardNavAutoDetect.setOnClickListener { navigateTo(com.smarthub.ui.autodetect.AutoDetectActivity::class.java) }
        
        // Debug tool is always accessible for developers
        binding.cardNavDebug.setOnClickListener { startActivity(android.content.Intent(this, com.smarthub.ui.debug.DebugRemoteActivity::class.java)) }
    }

    private fun hasHardware(): Boolean {
        // Returns true if phone has internal IR built-in OR a USB Dongle is detected
        return irManager.hasBuiltInIr() || irManager.hasUsbIr()
    }

    private fun navigateTo(activityClass: Class<*>) {
        if (!hasHardware()) {
            showConnectIrDialog()
            return // Block navigation
        }
        startActivity(android.content.Intent(this, activityClass))
    }

    private fun showConnectIrDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
            .setTitle("🔌 Hardware Disconnected")
            .setMessage("No built-in IR sensor or Type-C USB IR Blaster was detected on this device.\n\nPlease plug your IR transmitter into the charging port to use this remote.")
            .setPositiveButton("I Plugged It In") { dialog, _ ->
                if (hasHardware()) {
                    Toast.makeText(this, "Hardware Linked Successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error: Blaster still not detected.", Toast.LENGTH_LONG).show()
                    // Re-show dialog recursively if still failing
                    showConnectIrDialog() 
                }
            }
            .setNeutralButton("Use WiFi Hub Instead") { dialog, _ ->
               Toast.makeText(this, "Routing signals via ESP32 WiFi...", Toast.LENGTH_SHORT).show()
               dialog.dismiss() 
            }
            .setCancelable(false)
            .show()
    }
}
