package com.example.pomopet

import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.ActivityPetScreenBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class PetScreenActivity : AppCompatActivity() {
    var timerIds = Array<Int>(3){-1} // store ids since we will need to dynamically add and delete the views
    lateinit var animationDrawable: AnimationDrawable
    var handler = Handler(Looper.getMainLooper())

    companion object {
        var timerThread : CountDownTimer ? = null // this is so we can cancel the timer
        const val PET_NAME = "PET_NAME"
        const val EVOL = "EVOL" // current evolution
        const val PET_TYPE = "PET_TYPE"

        const val RED_PET = 0
        const val PURPLE_PET = 1
        const val ORANGE_PET = 2
    }

    var curPetType = -1

    // ----- Set countdown timer and updates the text in the timer
    fun timerThreadStart(hour: Long, min: Long, seconds: Long, hourText: TextView, minText: TextView, secText: TextView, petScreenBinding: ActivityPetScreenBinding){
        var finalMillis = (hour * 3600000) + (min * 60000) +(seconds * 1000)

        timerThread = object : CountDownTimer(finalMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                hourText.text = (millisUntilFinished / 3600000).toString()
                minText.text = ((millisUntilFinished % 3600000) / 60000).toString().padStart(2, '0')
                secText.text = ((millisUntilFinished % 60000) / 1000).toString().padStart(2, '0')
            }

            override fun onFinish() {
                // TO DO: to add code here, potentially giving exp?

                // Reset Timer and Buttons
                // Reset Buttons
                petScreenBinding.timerBtn0.isEnabled = false
                petScreenBinding.timerBtn1.isEnabled = true
                // Reset Timer
                // (1) Remove TextViews
                petScreenBinding.layoutTimer.removeViewAt(0) // hour
                petScreenBinding.layoutTimer.removeViewAt(1) // min
                petScreenBinding.layoutTimer.removeViewAt(2) // sec
                // (2) Add EditTexts
                // (2.1) Hour Input
                var hourInput_et = EditText(this@PetScreenActivity)
                hourInput_et.id = timerIds[0]
                hourInput_et.setLayoutParams(
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.5f
                    )
                )
                hourInput_et.setEms(10)
                hourInput_et.hint = "00h"
                hourInput_et.inputType = InputType.TYPE_CLASS_NUMBER
                hourInput_et.textAlignment = TEXT_ALIGNMENT_CENTER
                hourInput_et.textSize = 20f
                hourInput_et.typeface = Typeface.DEFAULT_BOLD
                petScreenBinding.layoutTimer.addView(hourInput_et, 0)
                // (2.2) Minute Input
                var minInput_et = EditText(this@PetScreenActivity)
                minInput_et.setLayoutParams(
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.5f
                    )
                )
                minInput_et.setEms(10)
                minInput_et.id = timerIds[1]
                minInput_et.hint = "00m"
                minInput_et.inputType = InputType.TYPE_CLASS_NUMBER
                minInput_et.textAlignment = TEXT_ALIGNMENT_CENTER
                minInput_et.textSize = 20f
                minInput_et.typeface = Typeface.DEFAULT_BOLD
                petScreenBinding.layoutTimer.addView(minInput_et, 2)
                // (2.3) Second Input
                var secInput_et = EditText(this@PetScreenActivity)
                secInput_et.id = timerIds[2]
                secInput_et.setLayoutParams(
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.5f
                    )
                )
                secInput_et.setEms(10)
                secInput_et.hint = "00s"
                secInput_et.inputType = InputType.TYPE_CLASS_NUMBER
                secInput_et.textAlignment = TEXT_ALIGNMENT_CENTER
                secInput_et.textSize = 20f
                secInput_et.typeface = Typeface.DEFAULT_BOLD
                petScreenBinding.layoutTimer.addView(secInput_et, 4)

                // Update Experience and Level of Pet
                updatePetExp(hour, min, seconds, petScreenBinding)

            }
        }.start()

    }

    fun setupTimer(petScreenBinding: ActivityPetScreenBinding, getHour: String, getMin: String, getSec: String)
    {
        // ----- This part deletes edit texts [inputs] and replaces it with the text view of the timer
        // NOTE: we don't store the ids here, only in cancelTimer when editTexts are involved
        // (aka we need to get their inputs)

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
            hourView, minView, secView, petScreenBinding)

        // ----- Disable start button and enable cancel button
        petScreenBinding.timerBtn0.isEnabled = true
        petScreenBinding.timerBtn1.isEnabled = false
    }

    fun cancelTimer(petScreenBinding: ActivityPetScreenBinding)
    {
        // ----- Cancel the timer thread
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

    // ----- Just a thread to make the pet move/animate
    fun petAnimationStart()
    {
        // run pet animation
        Thread {
            handler.post{
                animationDrawable.start()
            }
        }.start()

    }

    // ----- Set the right animation for current pet and evolution
    fun petTypeSet(iv: ImageView, petType: Int, evol: Int)
    {
        when (petType) {
            RED_PET->when(evol){
                1->iv.setImageResource(R.drawable.anim_evol1_red)
                2->iv.setImageResource(R.drawable.anim_evol2_red)
                3->iv.setImageResource(R.drawable.anim_evol3_red)
            }
            // purple
            PURPLE_PET->when(evol){
                1->iv.setImageResource(R.drawable.anim_evol1_purple)
                2->iv.setImageResource(R.drawable.anim_evol2_purple)
                3->iv.setImageResource(R.drawable.anim_evol3_purple)
            }
            ORANGE_PET->when(evol){
                1->iv.setImageResource(R.drawable.anim_evol1_orange)
                2->iv.setImageResource(R.drawable.anim_evol2_orange)
                3->iv.setImageResource(R.drawable.anim_evol3_orange)
            }
        }

    }

    private fun initRestorePetInfo(name: String, curExp: Int, maxExp: Int, level: Int, imagePet: ImageView, petScreenBinding: ActivityPetScreenBinding){
        // Retrieve Information
        // TO DO: Should use the local storage to retrieve all pet details of the player. (For MCO2, it's okay if not implemented yet)

        // Change details on screen to display current progress of the pet
        //petScreenBinding.imgPet.
        petScreenBinding.txtPetName.text = name // Pet Name
        petScreenBinding.txtLevel.text = "Level " + level.toString() // Pet Level

        // Experience Bar
        // Note: Always set the Max before the Progress. It matters.
        petScreenBinding.progressbarExp.max = maxExp // Maximum Exp to level up
        petScreenBinding.progressbarExp.progress = curExp // Current Exp

    }

    fun updatePetExp(hour: Long, min: Long, seconds: Long, petScreenBinding: ActivityPetScreenBinding){
        // Max. Experience per Level
        // Lv1 : 1000
        // Lv2 : 2000
        // Lv3 : 3000
        // Lv4 : 4000
        // LvN : N000

        // Level of Evolutions
        // Lv10 : 2nd Evolution
        // Lv20 : 3rd Evolution

        // Experience Computation
        // Per Second -> 0.5 Exp Earned
        // Example: 1hr Pomodoro -> 1,800 Exp Earned -> Lv1 to Lv2 (80% Filled Exp Bar)

        //If Hour is given, compute exp using hour
        //If Min is given, compute exp using minutes
        //If Seconds is given, compute exp using seconds
        // Retrieve Current Exp. Information
        val curLvl = petScreenBinding.txtLevel.text
        val curExp = petScreenBinding.progressbarExp.progress
        val maxExp = petScreenBinding.progressbarExp.max
        var earnedExp = 0.0
        if ( hour != 0.toLong() ){
            // Compute Exp
            earnedExp = ((hour * 3600) * 0.5)
            // If earned exp is over the maximum experience points, level up pet and add the remaining earned exp
            if ( (earnedExp + curExp.toLong()) >= maxExp.toLong() ){
                Toast.makeText(this, "Congratulations! Pet leveled up.", Toast.LENGTH_LONG).show()

                // Compute for remaining exp
                // Note: Absolutevalue property is used to make sure that the remaining exp is always a positive value
                val remainingExp = (maxExp.toLong() - (earnedExp + curExp.toLong())).absoluteValue

                // Since curLvl is a string, find the numbers in the string then use the number
                val extractedLvl = curLvl.filter { it.isDigit() }.toString()
                val updatedLvl = (extractedLvl.toIntOrNull()?.plus(1))
                petScreenBinding.txtLevel.text = "Level " + updatedLvl.toString()
                petScreenBinding.progressbarExp.max = petScreenBinding.progressbarExp.max.plus(1000) // Increase Exp. Bar by 1000
                petScreenBinding.progressbarExp.progress = 0 // Reset Exp. Bar

                // Check if pet needs to be evolved
                if ( updatedLvl == 10 ||
                    updatedLvl == 20 ){
                    when (updatedLvl) {
                        10 -> {
                            Toast.makeText(this, "Congratulations! Pet evolved to the 1st Evolution" , Toast.LENGTH_LONG).show()
                            petTypeSet(petScreenBinding.imgPet, curPetType, 2)
                        }
                        20 -> {
                            Toast.makeText(this, "Congratulations! Pet evolved to the 2nd Evolution" , Toast.LENGTH_LONG).show()
                            petTypeSet(petScreenBinding.imgPet, curPetType, 3)
                        }
                    }
                    animationDrawable = petScreenBinding.imgPet.drawable as AnimationDrawable
                    animationDrawable.stop()
                    petAnimationStart()
                }

                // Add remaining exp
                petScreenBinding.progressbarExp.progress = remainingExp.toInt()

            } else {
                // Add Earned Exp to Current Exp
                petScreenBinding.progressbarExp.progress = (earnedExp + curExp).roundToInt()
            }
        } else if ( min != 0.toLong() ){
            // Compute Exp
            earnedExp = ((min * 60) * 0.5)
            // If earned exp is over the maximum experience points, level up pet and add the remaining earned exp
            if ( (earnedExp + curExp.toLong() ) >= maxExp.toLong() ){
                Toast.makeText(this, "Congratulations! Pet leveled up.", Toast.LENGTH_LONG).show()

                // Compute for remaining exp
                // Note: Absolutevalue property is used to make sure that the remaining exp is always a positive value
                val remainingExp = (maxExp.toLong() - (earnedExp + curExp.toLong())).absoluteValue

                // Since curLvl is a string, find the numbers in the string then use the number
                val extractedLvl = curLvl.filter { it.isDigit() }.toString()
                val updatedLvl = (extractedLvl.toIntOrNull()?.plus(1))
                petScreenBinding.txtLevel.text = "Level " + updatedLvl.toString()
                petScreenBinding.progressbarExp.max = petScreenBinding.progressbarExp.max.plus(1000) // Increase Exp. Bar by 1000
                petScreenBinding.progressbarExp.progress = 0 // Reset Exp. Bar

                // Check if pet needs to be evolved
                if ( updatedLvl == 10 ||
                    updatedLvl == 20 ){
                    when (updatedLvl) {
                        10 -> {
                            Toast.makeText(this, "Congratulations! Pet evolved to the 1st Evolution" , Toast.LENGTH_LONG).show()
                            petTypeSet(petScreenBinding.imgPet, curPetType, 2)
                        }
                        20 -> {
                            Toast.makeText(this, "Congratulations! Pet evolved to the 2nd Evolution" , Toast.LENGTH_LONG).show()
                            petTypeSet(petScreenBinding.imgPet, curPetType, 3)
                        }
                    }

                    animationDrawable = petScreenBinding.imgPet.drawable as AnimationDrawable
                    animationDrawable.stop()
                    petAnimationStart()
                }

                // Add remaining exp
                petScreenBinding.progressbarExp.progress = remainingExp.toInt()
            } else {
                // Add Earned Exp to Current Exp
                petScreenBinding.progressbarExp.progress = (earnedExp + curExp).roundToInt()
            }

        } else if ( seconds != 0.toLong() ){
            // Compute Exp
            earnedExp = seconds * 0.5
            // If current exp is over the maximum experience points, level up pet and add the remaining earned exp
            if ( (earnedExp + curExp.toLong() ) >= maxExp.toLong() ){
                Toast.makeText(this, "Congratulations! Pet leveled up.", Toast.LENGTH_LONG).show()

                // Compute for remaining exp
                // Note: Absolutevalue property is used to make sure that the remaining exp is always a positive value
                val remainingExp = (maxExp.toLong() - (earnedExp + curExp.toLong())).absoluteValue

                // Since curLvl is a string, find the numbers in the string then use the number
                val extractedLvl = curLvl.filter { it.isDigit() }.toString()
                val updatedLvl = (extractedLvl.toIntOrNull()?.plus(1))
                petScreenBinding.txtLevel.text = "Level " + updatedLvl.toString()
                petScreenBinding.progressbarExp.max = petScreenBinding.progressbarExp.max.plus(1000) // Increase Exp. Bar by 1000
                petScreenBinding.progressbarExp.progress = 0 // Reset Exp. Bar

                // Check if pet needs to be evolved
                if ( updatedLvl == 10 ||
                    updatedLvl == 20 ){
                    when (updatedLvl) {
                        10 -> {
                            Toast.makeText(this, "Congratulations! Pet evolved to the 1st Evolution" , Toast.LENGTH_LONG).show()
                            Log.d("PetScreenActivity", PetScreenActivity.PET_TYPE.toString())
                            petTypeSet(petScreenBinding.imgPet, curPetType, 2)
                        }
                        20 -> {
                            Toast.makeText(this, "Congratulations! Pet evolved to the 2nd Evolution" , Toast.LENGTH_LONG).show()
                            petTypeSet(petScreenBinding.imgPet, curPetType, 3)
                        }
                    }

                    animationDrawable = petScreenBinding.imgPet.drawable as AnimationDrawable
                    animationDrawable.stop()
                    petAnimationStart()
                }

                // Add remaining exp
                petScreenBinding.progressbarExp.progress = remainingExp.toInt()
            } else {
                // Add Earned Exp to Current Exp
                petScreenBinding.progressbarExp.progress = (earnedExp + curExp).roundToInt()
            }
        }

        Toast.makeText(this, "Earned $earnedExp Exp Points!", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBind activity_pet_screen.xml
        val petScreenBinding: ActivityPetScreenBinding = ActivityPetScreenBinding.inflate(layoutInflater)
        // Change the scene
        setContentView(petScreenBinding.root)

        // Get Username, Pet Name, and Pet Type
        val loggedUsername = intent.getStringExtra(RegisterActivity.USERNAME)
        val petName = intent.getStringExtra(PetScreenActivity.PET_NAME)
        val petType = intent.getIntExtra(PetScreenActivity.PET_TYPE, -1)
        val petEvol = intent.getIntExtra(PetScreenActivity.EVOL, -1)

        curPetType = petType

        // Display a toast welcoming the user
        Toast.makeText(this, "Welcome to PomoPet, $loggedUsername!", Toast.LENGTH_SHORT).show()

        // Lines 26 to 33 is for testing purposes. You may delete it or comment it out.
        // Test Activity View Exercise (Delete this after)
        //val viewExerciseTemplateIntentActivity = Intent(applicationContext, ViewExerciseActivity::class.java)

        petScreenBinding.timerBtn0.backgroundTintList = this.resources.getColorStateList(R.color.timer_button_color, this.theme)
        petScreenBinding.timerBtn1.backgroundTintList = this.resources.getColorStateList(R.color.timer_button_color, this.theme)

        petScreenBinding.timerBtn0.isEnabled = false

        // ----- TO DO, restrict input / input filter for timer (max 12 hours, etc)
        // ----- We need to store the ids because of the setOnClickListener algorithm
        // ex. We cannot assume that "timerHrsinputEt" will always be present since we will be deleting it later on
        timerIds[0] = petScreenBinding.timerHrsinputEt.id
        timerIds[1] = petScreenBinding.timerMinsinputEt.id
        timerIds[2] = petScreenBinding.timerSecinputEt.id

        // ----- Cancel button; Change TextViews to EditText
        petScreenBinding.timerBtn0.setOnClickListener{
            // cancel
            cancelTimer(petScreenBinding)
        }

        // If timer start button is clicked, change EditText to TextViews
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

        // ----- Pet Archive Button
        petScreenBinding.petArchiveBtn.setOnClickListener{
            val petArchiveActivity = Intent(applicationContext, PetArchiveActivity::class.java)
            this.startActivity(petArchiveActivity)
            //this.startActivity(viewExerciseTemplateIntentActivity);
        }

        // ----- Pet Help Button
        petScreenBinding.helpBtn.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
            builder.setTitle("PomoPet Help")
            builder.setMessage(
                        "This game calculates experience points (EXP) for your pet based on time spent during activities such as Pomodoro sessions. Here's how it works:\n\n" +
                        "Maximum Experience per Level:\n" +
                        "Level 1: 1,000 EXP\n" +
                        "Level 2: 2,000 EXP\n" +
                        "Level 3: 3,000 EXP\n" +
                        "Level 4: 4,000 EXP\n" +
                        "Level N: N * 1000 EXP (i.e., the maximum EXP for a given level is the level number multiplied by 1,000).\n\n" +
                        "Evolutions:\n" +
                        "Level 10: 1st Evolution\n" +
                        "Level 20: 2nd Evolution\n\n" +
                        "Experience Computation:\n" +
                        "Per second: You earn 0.5 EXP for each second of activity.\n\n" +
                        "Example Calculation:\n" +
                        "A 1-hour Pomodoro session (3,600 seconds) would earn you:\n" +
                        "3,600 × 0.5 = 1,800 EXP\n" +
                        "This is enough to level up from Level 1 to Level 2.\n\n" +
                        "EXP Calculation Based on Time:\n" +
                        "[1] If the time is given in hours, the system will compute the total EXP based on the number of hours.\n" +
                        "[2] If the time is given in minutes, the system will compute EXP based on minutes.\n" +
                        "[3] If the time is given in seconds, the system will compute EXP based on the exact number of seconds.\n\n" +
                        "You can use this system to track your pet's progress, level it up, and unlock evolutions!"
            )

            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                //Toast.makeText(applicationContext, android.R.string.ok, Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }

        // ----- Set the correct animation for the pet
        petTypeSet(petScreenBinding.imgPet, petType, petEvol)

        // ----- Run animation
        animationDrawable = petScreenBinding.imgPet.drawable as AnimationDrawable
        petAnimationStart()

        // ----- Set values for pet details
        petScreenBinding.txtUsername.text = loggedUsername
        initRestorePetInfo(petName.toString(), 0, 1000, 1, petScreenBinding.imgPet, petScreenBinding)
        
        // View Pet Archive


    }

    // ----- This is to stop the threads prior to finishing the activity
    override fun onDestroy() {
        super.onDestroy()
        animationDrawable.stop()
        handler.removeCallbacksAndMessages(null)
    }
}