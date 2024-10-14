package com.example.pomopet

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.ActivityPetScreenBinding

class PetScreenActivity : AppCompatActivity() {
    var timerIds = Array<Int>(3){-1} // store ids since we will need to dynamically add and delete the views

    companion object { // this is so we can cancel the timer
        var timerThread : CountDownTimer ? = null
    }

    fun timerThreadStart(hour: Long, min: Long, seconds: Long, hourText: TextView, minText: TextView, secText: TextView){
        var finalMillis = (hour * 3600000) + (min * 60000) +(seconds * 1000)

        timerThread = object : CountDownTimer(finalMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                hourText.text = (millisUntilFinished / 3600000).toString()
                minText.text = ((millisUntilFinished % 3600000) / 60000).toString().padStart(2, '0')
                secText.text = ((millisUntilFinished % 60000) / 1000).toString().padStart(2, '0')
            }

            override fun onFinish() {
                // to add code here, potentially giving exp?
            }
        }.start()

    }
    fun setupTimer(petScreenBinding: ActivityPetScreenBinding, getHour: String, getMin: String, getSec: String)
    {
        // ----- This part deletes edit texts [inputs] and replaces it with the text view of the timer
        // NOTE: we don't store the ids here, only in cancelTimer when editTexts are applicable
        // make inputBox (hour) to text view
        petScreenBinding.layoutTimer.removeViewAt(0)
        var hourView = TextView(this)
        hourView.text = getHour
        hourView.textSize = 20f
        hourView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        hourView.textAlignment = TEXT_ALIGNMENT_CENTER
        hourView.setTypeface(null, Typeface.BOLD);
        petScreenBinding.layoutTimer.addView(hourView, 0)

        // make inputBox (minute) to text view
        petScreenBinding.layoutTimer.removeViewAt(2)
        var minView = TextView(this)
        minView.text = getMin.padStart(2, '0')
        minView.textSize = 20f
        minView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        minView.textAlignment = TEXT_ALIGNMENT_CENTER
        minView.setTypeface(null, Typeface.BOLD);
        petScreenBinding.layoutTimer.addView(minView, 2)

        // make inputBox (seconds) to text view
        petScreenBinding.layoutTimer.removeViewAt(4)
        var secView = TextView(this)
        secView.text = getSec.padStart(2, '0')
        secView.textSize = 20f
        secView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        secView.textAlignment = TEXT_ALIGNMENT_CENTER
        secView.setTypeface(null, Typeface.BOLD);
        petScreenBinding.layoutTimer.addView(secView, 4)

        // ----- Timer thread starts
        timerThreadStart(getHour.toLong(),
            getMin.toLong(),
            getSec.toLong(),
            hourView, minView, secView)

        // ----- Disable start button and enable cancel button
        petScreenBinding.timerBtn0.isEnabled = true
        petScreenBinding.timerBtn1.isEnabled = false
    }

    fun cancelTimer(petScreenBinding: ActivityPetScreenBinding)
    {
        // Cancel the timer
        timerThread?.cancel()

        // ----- Change text views to edit text
        petScreenBinding.layoutTimer.removeViewAt(0)
        var hourView = EditText(this)
        hourView.inputType = InputType.TYPE_CLASS_NUMBER
        hourView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        hourView.textSize = 20f
        hourView.id = View.generateViewId()
        timerIds[0] = hourView.id
        hourView.textAlignment = TEXT_ALIGNMENT_CENTER
        hourView.setTypeface(null, Typeface.BOLD)
        hourView.hint = "00h"
        petScreenBinding.layoutTimer.addView(hourView, 0)

        // make inputBox (minute) to text view
        petScreenBinding.layoutTimer.removeViewAt(2)
        var minView = EditText(this)
        minView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        minView.inputType = InputType.TYPE_CLASS_NUMBER
        minView.textSize = 20f
        minView.id = View.generateViewId()
        timerIds[1] = minView.id
        minView.textAlignment = TEXT_ALIGNMENT_CENTER
        minView.setTypeface(null, Typeface.BOLD)
        minView.hint = "00m"
        petScreenBinding.layoutTimer.addView(minView, 2)

        // make inputBox (seconds) to text view
        petScreenBinding.layoutTimer.removeViewAt(4)
        var secView = EditText(this)
        secView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        secView.inputType = InputType.TYPE_CLASS_NUMBER
        secView.textSize = 20f
        secView.id = View.generateViewId()
        timerIds[2] = secView.id
        secView.textAlignment = TEXT_ALIGNMENT_CENTER
        secView.setTypeface(null, Typeface.BOLD)
        secView.hint = "00s"
        petScreenBinding.layoutTimer.addView(secView, 4)

        // ----- Disable start button and enable cancel button
        petScreenBinding.timerBtn0.isEnabled = false
        petScreenBinding.timerBtn1.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBind activity_pet_screen.xml
        val petScreenBinding: ActivityPetScreenBinding = ActivityPetScreenBinding.inflate(layoutInflater)
        // Change the scene
        setContentView(petScreenBinding.root)
        // Get Username
        val loggedUsername = intent.getStringExtra(RegisterActivity.USERNAME)
        // Display a toast welcoming the user
        Toast.makeText(this, "Welcome to PomoPet, $loggedUsername!", Toast.LENGTH_SHORT).show()

        // Lines 26 to 33 is for testing purposes. You may delete it or comment it out.
        // Test Activity View Exercise (Delete this after)
        val viewExerCiseTemplateIntentActivity = Intent(applicationContext, ViewExerciseActivity::class.java)

        petScreenBinding.timerBtn0.backgroundTintList = this.resources.getColorStateList(R.color.timer_button_color, this.theme)
        petScreenBinding.timerBtn1.backgroundTintList = this.resources.getColorStateList(R.color.timer_button_color, this.theme)

        petScreenBinding.timerBtn0.isEnabled = false

        // ----- TO DO, restrict input / input filter
        // ----- We need to store the ids because of the setOnClickListener algorithm
        // ex. We cannot assume that "timerHrsinputEt" will always be present since we will be deleting it later on
        timerIds[0] = petScreenBinding.timerHrsinputEt.id
        timerIds[1] = petScreenBinding.timerMinsinputEt.id
        timerIds[2] = petScreenBinding.timerSecinputEt.id

        // If timer start button is clicked, test recycler view
        petScreenBinding.timerBtn1.setOnClickListener{
            var getHour = findViewById<EditText>(timerIds[0]).text.toString()
            var getMin = findViewById<EditText>(timerIds[1]).text.toString()
            var getSec = findViewById<EditText>(timerIds[2]).text.toString()

            if (getHour.isEmpty())
                Toast.makeText(this, "Missing number of hours.", Toast.LENGTH_SHORT).show()
            else if (getMin.isEmpty())
                Toast.makeText(this, "Missing number of minutes.", Toast.LENGTH_SHORT).show()
            else if (getSec.isEmpty())
                Toast.makeText(this, "Missing number of seconds.", Toast.LENGTH_SHORT).show()
            else if (getMin.toString().toInt() < 0 || getMin.toString().toInt() > 59 )
                Toast.makeText(this, "Minutes must be in range of 0 to 59.", Toast.LENGTH_SHORT).show()
            else if (getSec.toString().toInt() < 0 || getSec.toString().toInt() > 59 )
                Toast.makeText(this, "Seconds must be in range of 0 to 59.", Toast.LENGTH_SHORT).show()
            else if (getHour.toString().toInt() == 0 && getMin.toString().toInt() == 0 && getSec.toString().toInt() == 0)
                Toast.makeText(this, "Cannot set timer to 0h 0m 0s.", Toast.LENGTH_SHORT).show()
            // start timer
            else
                setupTimer(petScreenBinding, getHour, getMin, getSec)

        }

        // ----- FOR TESTING: Moved button for exercise recycler view to here
        petScreenBinding.btnExerciseTest.setOnClickListener{
            this.startActivity(viewExerCiseTemplateIntentActivity);
        }

        petScreenBinding.timerBtn0.setOnClickListener{
            // cancel
            cancelTimer(petScreenBinding)
        }



        petScreenBinding.txtUsername.text = loggedUsername






    }
}





