package com.smarthub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smarthub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardNavTv.setOnClickListener { startActivity(android.content.Intent(this, com.smarthub.ui.tv.TvRemoteActivity::class.java)) }
        binding.cardNavAc.setOnClickListener { startActivity(android.content.Intent(this, com.smarthub.ui.ac.AcRemoteActivity::class.java)) }
        binding.cardNavProjector.setOnClickListener { startActivity(android.content.Intent(this, com.smarthub.ui.projector.ProjectorRemoteActivity::class.java)) }
        binding.cardNavSoundbar.setOnClickListener { startActivity(android.content.Intent(this, com.smarthub.ui.soundbar.SoundbarRemoteActivity::class.java)) }
        binding.cardNavLights.setOnClickListener { startActivity(android.content.Intent(this, com.smarthub.ui.lights.LightsRemoteActivity::class.java)) }
        binding.cardNavAutoDetect.setOnClickListener { startActivity(android.content.Intent(this, com.smarthub.ui.autodetect.AutoDetectActivity::class.java)) }
        binding.cardNavDebug.setOnClickListener { startActivity(android.content.Intent(this, com.smarthub.ui.debug.DebugRemoteActivity::class.java)) }
    }
}
