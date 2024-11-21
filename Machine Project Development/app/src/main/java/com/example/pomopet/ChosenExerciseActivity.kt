package com.example.pomopet


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pomopet.databinding.ChosenExerciseBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


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

        // Reset variables
        exerCount = 0
        levelScalar = 0
        lastIncrementTime = 0

        // Get the exercise details from intent
        currentExercise = intent.getStringExtra("EXER_NAME") ?: "Unknown Exercise"

        // Initialize the sensors
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Use view binding to set the data to the views
        viewBinding.chosenName.text = currentExercise
        viewBinding.chosenIcon.setImageResource(intent.getIntExtra("EXER_ICON", 0))


        // If the user does not want to finish/perform the exercise
        viewBinding.finishBtn.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
            builder.setTitle("Warning")

            builder.setMessage("Finishing will only give you x1 level up. Are you sure you want to proceed?")
                .setPositiveButton("Yes") { dialog, which ->

                    hideSystemBars()
                    // Reset variables
                    exerCount = 0
                    levelScalar = 0
                    lastIncrementTime = 0

                    // Send single level-up result
                    val intent = Intent()
                    intent.putExtra("LEVEL_SCALAR", 1)  // Indicate single level-up (x1 bonus)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                .setNegativeButton("No") { dialog, which ->
                    hideSystemBars()
                    dialog.dismiss()
                }
                .show()

        }

        viewBinding.btnChosenExercise.setOnClickListener{

            val inflater = LayoutInflater.from(this)
            val viewInitialized = inflater.inflate(R.layout.builder_exercise_video_desc, null)

            val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
            builder.setTitle(currentExercise)
            builder.setView(viewInitialized)
            val video_exer = viewInitialized.findViewById<WebView>(R.id.video_exer)
            val txt_help_exer = viewInitialized.findViewById<TextView>(R.id.txt_help_exer)

            video_exer.settings.javaScriptEnabled = true
            video_exer.loadData(intent.getStringExtra("EXER_VID") ?: "No Video", "text/html", "UTF-8")
            txt_help_exer.setText(intent.getStringExtra("EXER_DESC") ?: "No Description")

            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                //Toast.makeText(applicationContext, android.R.string.ok, Toast.LENGTH_SHORT).show()
                hideSystemBars()
            }
            builder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()

        // Register sensors
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (accelerometerSensor == null) {
            Log.e("SensorStatus", "Accelerometer not available!")
        }
        if (gyroscopeSensor == null) {
            Log.e("SensorStatus", "Gyroscope not available!")
        }

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    Log.d("CurrentExercise", "Exercise selected: $currentExercise")
                    Log.d("RawSensorData", "Sensor: ${event.sensor.name}, x: ${event.values[0]}, y: ${event.values[1]}, z: ${event.values[2]}")
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
        if (currentTime - lastIncrementTime < 50000) return // Ignore if within 50000ms

        Log.d("ExerciseDebug", "currentExercise: $currentExercise, x: $x, y: $y, z: $z, isGyroscope: $isGyroscope")

        when (currentExercise) {
            "Jumping Jacks" -> {
                if (!isGyroscope && z > 10) { // Use accelerometer for Jumping Jacks
                    Log.d("SensorData", "x: $x, y: $y, z: $z")
                    levelScalar = 1
                    exerCount++
                    updateExerciseCounter(exerCount)

                    // Finishing the exercise
                    if (exerCount == 10){
                        levelScalar = 2
                        finishExer()
                    }
                }
            }
            // Works
            "Squats" -> {
                if (!isGyroscope) { // Use accelerometer for Squats
                    if (y > 10) {
                        // Squat up logic

                        levelScalar = 1
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
            // Works when you move the phone, pero rotate ???
            "Lunges" -> {
                if (isGyroscope) { // Use gyroscope for Lunges
                    if (x > 6 || x < -6) {

                        levelScalar = 1
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

    private fun updateExerciseCounter(exerCount: Int) {
        Log.d("ChosenExerciseActivity", "Repetition count updated: $exerCount") // not being logged

        viewBinding.exerCounter.text = "Repetitions: $exerCount"

    }

    private fun finishExer(){
        Toast.makeText(this, "EXERCISE FINISHED!", Toast.LENGTH_SHORT).show()

        Log.d("ChosenExerciseActivity", "LevelScalar Value: $levelScalar")

        // Send the repetitions count back to the calling activity
        val intent = Intent()
        intent.putExtra("LEVEL_SCALAR", 2)  // Indicate double level-up (x2 bonus)
        setResult(RESULT_OK, intent)

        // Reset variables
        exerCount = 0
        levelScalar = 0
        lastIncrementTime = 0

        finish()
    }
}