package com.example.pomopet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsBinding: ActivitySettingsBinding =ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)
        settingsBinding.txtXBtn.setOnClickListener{
            finish()
        }
    }
}