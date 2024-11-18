package com.example.pomopet

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pomopet.databinding.ChosenExerciseBinding

class ChosenExerciseActivity : AppCompatActivity() {

    companion object {
        const val RESULT_KEY = "RESULT_KEY"
    }

    // Initialize chosen record
    private lateinit var viewBinding: ChosenExerciseBinding

    // Initialize for sensors
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorEventListener: SensorEventListener
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    // Variable to hold the exercise type
    private var currentExercise: String? = null

    // Variable to count number of repetitions
    private var exerCount = 0

    // Variable to track type of level up
    private var levelScalar = 0

    private var lastIncrementTime = 0L

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

        val webView = WebView(this)
        webView.clearCache(true)

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

        // If the user does not want to finish/perform the exercise
        viewBinding.finishBtn.setOnClickListener {
            levelScalar = 1
            finishExer()
        }
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
                        // For squats and jumping jacks for linear movement
                        Sensor.TYPE_ACCELEROMETER -> {
                            // Pass accelerometer data to determineExercise
                            val x = event.values[0]
                            val y = event.values[1]
                            val z = event.values[2]
                            determineExercise(currentExercise, x, y, z, isGyroscope = false)
                        }
                        // For lunges exercise to measure rotational movement
                        Sensor.TYPE_GYROSCOPE -> {
                            // Pass gyroscope data to determineExercise if needed
                            val x = event.values[0]
                            val y = event.values[1]
                            val z = event.values[2]
                            determineExercise(currentExercise, x, y, z, isGyroscope = true)
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

    private fun determineExercise(currentExercise: String?, x: Float, y: Float, z: Float, isGyroscope: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastIncrementTime < 500000) return // Ignore if within 500ms

        when (currentExercise) {
            "Jumping Jacks" -> {
                if (!isGyroscope && z > 9) { // Use accelerometer for Jumping Jacks
                    Log.d("SensorData", "x: $x, y: $y, z: $z")
                    exerCount++
                    updateExerciseCounter(exerCount)

                    // Finishing the exercise
                    if (exerCount == 10){
                        levelScalar = 2
                        finishExer()
                    }
                }
            }
            "Squats" -> {
                if (!isGyroscope) { // Use accelerometer for Squats
                    /*if (y < -5) {
                        // Squat down logic
                        // is this needed? what if we count nalang the times the user squats up?
                    }*/
                    if (y > 9) {
                        // Squat up logic
                        Log.d("SensorData", "x: $x, y: $y, z: $z")
                        exerCount++
                        updateExerciseCounter(exerCount)

                        // Finishing the exercise
                        if (exerCount == 10){
                            levelScalar = 2
                            finishExer()
                        }
                    }
                }
            }
            "Lunges" -> {
                if (isGyroscope) { // Use gyroscope for Lunges
                    if (x > 9 || x < -9) {
                        Log.d("SensorData", "x: $x, y: $y, z: $z")
                        exerCount++
                        updateExerciseCounter(exerCount)

                        // Finishing the exercise
                        if (exerCount == 10){
                            levelScalar = 2
                            finishExer()
                        }
                    }
                }
            }
        }
    }

    private fun updateExerciseCounter(count: Int) {
        Log.d("ChosenExerciseActivity", "Repetition count updated: $count")
        runOnUiThread {
            viewBinding.exerCounter.text = "Repetitions: $count"
        }
    }

    private fun finishExer(){
        Toast.makeText(this, "EXERCISE FINISHED!", Toast.LENGTH_SHORT).show()

        // Send the repetitions count back to the calling activity
        val returnIntent = Intent()
        returnIntent.putExtra("LEVEL_SCALAR", levelScalar)  // Pass level-up scalar (1 for single, 2 for double)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}