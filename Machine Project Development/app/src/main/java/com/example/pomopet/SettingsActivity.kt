package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pomopet.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private var currentPomodoroIndex = -1
    private var currentBreakIndex = -1
    private var volumeLevel = -1f

    companion object {
        val POMODOROS_SETTINGS = "POMODOROS_SETTINGS"
        val POMO_BREAK_DURATION_SETTINGS = "POMO_BREAK_DURATION_SETTINGS"
        val VOLUME_SETTINGS = "VOLUME_SETTINGS"
        val FILE_SETTINGS = "FILE_SETTINGS"

        val pomodoro_set_array = arrayOf(1, 2, 3, 4, 5)
        val break_duration_array = arrayOf(5, 10, 15, 20, 25, 30)
    }

    private fun hideSystemBars() {
        val controller = WindowInsetsControllerCompat(
            window, window.decorView
        )

        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // settings sharedpreferences, separate from file_pet
        val sharedPreferences = getSharedPreferences(FILE_SETTINGS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val settingsBinding: ActivitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)

        currentPomodoroIndex = sharedPreferences.getInt(POMODOROS_SETTINGS, 0)
        currentBreakIndex = sharedPreferences.getInt(POMO_BREAK_DURATION_SETTINGS, 2)

        settingsBinding.txtPomodorosDuration.setText(pomodoro_set_array[currentPomodoroIndex].toString())
        settingsBinding.txtBreakMin.setText(break_duration_array[currentBreakIndex].toString())
        settingsBinding.sliderVolume.setValues(sharedPreferences.getFloat(VOLUME_SETTINGS, 50f))

        volumeLevel = sharedPreferences.getFloat(VOLUME_SETTINGS, 50f) // need this to get the value from slider (addOnChangeListener)

        settingsBinding.sliderVolume.addOnChangeListener{slider, value, fromUser ->
            volumeLevel = value
        }

        settingsBinding.btnPomoBack.setOnClickListener{
            if (currentPomodoroIndex - 1 < 0)
                currentPomodoroIndex = pomodoro_set_array.size - 1
            else
                currentPomodoroIndex = currentPomodoroIndex - 1

            settingsBinding.txtPomodorosDuration.setText(pomodoro_set_array[currentPomodoroIndex].toString())
        }

        settingsBinding.btnPomoForward.setOnClickListener{
            if (currentPomodoroIndex + 1 > pomodoro_set_array.size - 1)
                currentPomodoroIndex = 0
            else
                currentPomodoroIndex = currentPomodoroIndex + 1

            settingsBinding.txtPomodorosDuration.setText(pomodoro_set_array[currentPomodoroIndex].toString())
        }

        settingsBinding.btnBreakBack.setOnClickListener{
            if (currentBreakIndex - 1 < 0)
                currentBreakIndex = break_duration_array.size - 1
            else
                currentBreakIndex = currentBreakIndex - 1

            settingsBinding.txtBreakMin.setText(break_duration_array[currentBreakIndex].toString())
        }

        settingsBinding.btnBreakForward.setOnClickListener{
            if (currentBreakIndex + 1 > break_duration_array.size - 1)
                currentBreakIndex = 0
            else
                currentBreakIndex = currentBreakIndex + 1

            settingsBinding.txtBreakMin.setText(break_duration_array[currentBreakIndex].toString())
        }

        settingsBinding.txtXBtn.setOnClickListener{
            finish()
        }

        settingsBinding.btnResetSettings.setOnClickListener{
            currentPomodoroIndex = 2
            currentBreakIndex = 0

            settingsBinding.txtPomodorosDuration.setText(pomodoro_set_array[currentPomodoroIndex].toString())
            settingsBinding.txtBreakMin.setText(break_duration_array[currentBreakIndex].toString())
            settingsBinding.sliderVolume.setValues(50f)

            editor.putFloat(VOLUME_SETTINGS, 50f)
            editor.putInt(POMODOROS_SETTINGS, currentPomodoroIndex)
            editor.putInt(POMO_BREAK_DURATION_SETTINGS, currentBreakIndex)
            editor.apply()

            val intentBGMService = Intent(this, BGMService::class.java)
            intentBGMService.putExtra("SIGNAL_KEY", "volume")
            startService(intentBGMService)

            Toast.makeText(this, "Successfully reset settings!", Toast.LENGTH_SHORT).show()
        }

        settingsBinding.btnSaveSettings.setOnClickListener{
            editor.putFloat(VOLUME_SETTINGS, volumeLevel)
            editor.putInt(POMODOROS_SETTINGS, currentPomodoroIndex)
            editor.putInt(POMO_BREAK_DURATION_SETTINGS, currentBreakIndex)
            editor.apply()
            val intentBGMService = Intent(this, BGMService::class.java)
            intentBGMService.putExtra("SIGNAL_KEY", "volume")
            startService(intentBGMService)
            Toast.makeText(this, "Successfully saved settings!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

}