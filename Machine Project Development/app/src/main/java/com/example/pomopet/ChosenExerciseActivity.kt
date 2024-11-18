package com.example.pomopet

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pomopet.databinding.ChosenExerciseBinding

class ChosenExerciseActivity : AppCompatActivity() {

    // Initialize chosen record
    private lateinit var viewBinding: ChosenExerciseBinding

    // Initialize for sensors
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorEventListener: SensorEventListener
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    // Variable to hold the exercise type
    private var currentExercise: String? = null

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
        viewBinding = ChosenExerciseBinding.inflate(layoutInflater) // Inflate the binding
        setContentView(viewBinding.root) // Set the content view to the binding's root

        // Get the exercise details from intent
        currentExercise = intent.getStringExtra("EXER_NAME") ?: "Unknown Exercise"

        // Initialize the sensors
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Use view binding to set the data to the views
        viewBinding.chosenName.text = currentExercise
        viewBinding.chosenIcon.setImageResource(intent.getIntExtra("EXER_ICON", 0))
        viewBinding.chosenDesc.text = intent.getStringExtra("EXER_DESC") ?: "No Description"
        viewBinding.chosenVid.settings.javaScriptEnabled = true
        viewBinding.chosenVid.loadData(intent.getStringExtra("EXER_VID") ?: "No Video", "text/html", "UTF-8")
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()

        // Register sensors
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    when (event.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            // Process accelerometer data based on the exercise type
                            val x = event.values[0]
                            val y = event.values[1]
                            val z = event.values[2]
                            detectExercise(currentExercise, x, y, z)
                        }
                        Sensor.TYPE_GYROSCOPE -> {
                            // Process gyroscope data if needed (e.g., for lunges)
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handle accuracy changes if needed
            }
        }

        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(sensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun detectExercise(currentExercise: String?, x: Float, y: Float, z: Float) {
        when (currentExercise) {
            "Jumping Jacks" -> {
                // Detect Jumping Jack based on Z-axis movement
                if (z > 15) {
                    // Jumping Jack logic
                }
            }
            "Squats" -> {
                // Detect Squat based on Y-axis movement
                if (y < -5) {
                    // Squat down logic
                }
                if (y > 5) {
                    // Squat up logic
                }
            }
            "Lunges" -> {
                // Detect Lunge based on X-axis or rotation (Gyroscope)
                if (x > 5 || x < -5) {
                    // Lunge detection logic
                }
            }
            else -> {
                // Default or unknown exercise detection
            }
        }
    }
    

}