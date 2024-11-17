package com.example.pomopet

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    companion object {
        val POMO_BREAK_DURATION_SETTINGS = "POMO_BREAK_DURATION_SETTINGS"
        val VOLUME_SETTINGS = "VOLUME_SETTINGS"
        val FILE_SETTINGS = "FILE_SETTINGS"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // settings sharedpreferences, separate from file_pet
        val sharedPreferences = getSharedPreferences(FILE_SETTINGS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val settingsBinding: ActivitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)

        settingsBinding.etBreakDuration.setText(sharedPreferences.getInt(POMO_BREAK_DURATION_SETTINGS, 5).toString())
        settingsBinding.sliderVolume.setValues(sharedPreferences.getFloat(VOLUME_SETTINGS, 50f))

        var volumeLevel = sharedPreferences.getFloat(VOLUME_SETTINGS, 50f) // need this to get the value from slider (addOnChangeListener)

        settingsBinding.sliderVolume.addOnChangeListener{slider, value, fromUser ->
            volumeLevel = value

        }

        setContentView(settingsBinding.root)

        settingsBinding.txtXBtn.setOnClickListener{
            finish()
        }

        settingsBinding.btnResetSettings.setOnClickListener{

            settingsBinding.etBreakDuration.setText("5")
            settingsBinding.sliderVolume.setValues(50f)

            editor.putFloat(VOLUME_SETTINGS, 5f)
            editor.putInt(POMO_BREAK_DURATION_SETTINGS, 5)
            editor.apply()

            Toast.makeText(this, "Successfully reset settings!", Toast.LENGTH_SHORT).show()
        }

        settingsBinding.btnSaveSettings.setOnClickListener{
            editor.putFloat(VOLUME_SETTINGS, volumeLevel)
            editor.putInt(POMO_BREAK_DURATION_SETTINGS, settingsBinding.etBreakDuration.text.toString().toInt())
            editor.apply()

            Toast.makeText(this, "Successfully saved settings!", Toast.LENGTH_SHORT).show()
        }
    }
}