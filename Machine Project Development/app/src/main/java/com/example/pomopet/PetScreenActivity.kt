package com.example.pomopet

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.HandlerThread
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pomopet.SettingsActivity.Companion.FILE_SETTINGS
import com.example.pomopet.SettingsActivity.Companion.POMODOROS_SETTINGS
import com.example.pomopet.SettingsActivity.Companion.POMO_BREAK_DURATION_SETTINGS
import com.example.pomopet.databinding.ActivityPetScreenBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class PetScreenActivity : AppCompatActivity() {

    companion object {
        var timerThread : CountDownTimer ? = null       // this is so we can cancel the timer

        // for a synchronized naming convention for intents
        const val PET_NAME = "PET_NAME"                 // string
        const val PET_EVOL = "PET_EVOL"                 // int
        const val PET_TYPE = "PET_TYPE"                 // int
        const val USERNAME = "USERNAME"                 // string

        // pet types
        const val RED_PET = 0
        const val PURPLE_PET = 1
        const val ORANGE_PET = 2
    }

    // db helper + current pet of user
    private lateinit var pomoDBHelper: PomoDBHelper
    private lateinit var currentPet: PomoModel

    private var timerIds = Array(3){-1} // store ids since we will need to dynamically add and delete the views
    private lateinit var animationDrawable: AnimationDrawable

    private var handlerThread = HandlerThread("AnimationThread").apply {start()}
    private var handler = Handler(handlerThread.looper)


    // screen binding
    private lateinit var petScreenBinding: ActivityPetScreenBinding

    // Level Up Related Variables
    private var extractedLvl: String = ""
    private var remainingExp: Double = 0.0
    private lateinit var levelUpTextView: TextView
    private var isLevelUpUIExists = false

    // timer variables
    private var remainingMillisTimer = 0L
    private var originalMillisTimer = 0L
    private var isPomoRunning = false
    private var isBreakRunning = false
    private lateinit var hourView: TextView
    private lateinit var minView: TextView
    private lateinit var secView: TextView
    private var initiatedPause = -1 // which timer triggered the pause; 0 is pomodoro, 1 is break

    // settings variables
    private var currentPomodoro = -1
    private var currentBreakDuration = -1
    private var settingsPomodoro = -1
    private var settingsBreakDuration = -1

    private lateinit var intentBGMService: Intent

    private val settingsActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result:ActivityResult ->
        loadSettings() // always reload settings for pomodoro and break duration
    }

    private val levelUpActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK){

            val levelScalar = result.data!!.getIntExtra("LEVEL_SCALAR", 0)

            Log.d("PetScreenActivity", "LevelScalar: $levelScalar")

            // Level up logic
            val updatedLvl = (extractedLvl.toIntOrNull()?.plus(levelScalar))
            petScreenBinding.txtLevel.text = "Level " + updatedLvl.toString()
            currentPet.pet_level = updatedLvl!!

            Log.d("levelUpActivityLauncher", "updatedLvl: " + updatedLvl)

            val nextMaxExp = petScreenBinding.progressbarExp.max.plus((1000*levelScalar)) // Increase Exp. Bar by 1000
            petScreenBinding.progressbarExp.max = nextMaxExp
            currentPet.pet_max_exp = nextMaxExp

            petScreenBinding.progressbarExp.progress = 0 // Reset Exp. Bar

            if (levelScalar == 1)
                Toast.makeText(this, "x1 Level up!", Toast.LENGTH_SHORT).show()

            else if (levelScalar == 2)
                Toast.makeText(this, "Congratulations! x2 Level up!", Toast.LENGTH_SHORT).show()


            // Check if pet needs to be evolved
            if (updatedLvl >= 10 && currentPet.pet_evol == 1) {
                Toast.makeText(this, "Congratulations! ${currentPet.pet_name} evolved to the 1st Evolution!" , Toast.LENGTH_LONG).show()
                Log.d("PetScreenActivity", PET_TYPE)
                petTypeSet(petScreenBinding.imgPet, currentPet.pet_type, 2)
                currentPet.pet_evol = 2

                petAnimationStopAndStartNewLooper()
                petAnimationStart()
            }
            else if (updatedLvl >= 20 && currentPet.pet_evol <= 2)
            {
                Toast.makeText(this, "Congratulations! ${currentPet.pet_name} evolved to the 2nd Evolution!" , Toast.LENGTH_LONG).show()
                petTypeSet(petScreenBinding.imgPet, currentPet.pet_type, 3)
                currentPet.pet_evol = 3

                petAnimationStopAndStartNewLooper()
                petAnimationStart()
            }

            // Add remaining exp
            petScreenBinding.progressbarExp.progress = remainingExp.toInt()
            currentPet.pet_exp = remainingExp.toInt()

            Log.d("levelUpActivityLauncher", "remainingExp: " + remainingExp.toInt())

            // save all changes
            pomoDBHelper.savePetExp(currentPet)
            pomoDBHelper.resetLevelUp(currentPet)

            removeLevelUpNotif()

            // resume timer
            if (currentPomodoro > 0) {
                if (initiatedPause == 0)
                    timerThreadStart(remainingMillisTimer)
                else if (initiatedPause == 1)
                    breakThreadStart(remainingMillisTimer)
            }
        }
    }

    private fun hideSystemBars() {
        val controller = WindowInsetsControllerCompat(
            window, window.decorView
        )

        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun changeTimerToEditText(){
        // Reset Timer
        // (1) Remove TextViews
        petScreenBinding.layoutTimer.removeViewAt(0) // hour
        petScreenBinding.layoutTimer.removeViewAt(1) // min
        petScreenBinding.layoutTimer.removeViewAt(2) // sec
        // (2) Add EditTexts
        // (2.1) Hour Input
        val hourInput_et = EditText(this@PetScreenActivity)
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
        hourInput_et.textSize = 28f
        hourInput_et.typeface = Typeface.DEFAULT_BOLD
        hourInput_et.filters += InputFilter.LengthFilter(1)
        petScreenBinding.layoutTimer.addView(hourInput_et, 0)

        // (2.2) Minute Input
        val minInput_et = EditText(this@PetScreenActivity)
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
        minInput_et.textSize = 28f
        minInput_et.typeface = Typeface.DEFAULT_BOLD
        minInput_et.filters += InputFilter.LengthFilter(2)
        petScreenBinding.layoutTimer.addView(minInput_et, 2)
        // (2.3) Second Input
        val secInput_et = EditText(this@PetScreenActivity)
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
        secInput_et.textSize = 28f
        secInput_et.typeface = Typeface.DEFAULT_BOLD
        secInput_et.filters += InputFilter.LengthFilter(2)
        petScreenBinding.layoutTimer.addView(secInput_et, 4)
    }

    // ----- Set countdown timer and updates the text in the timer
    private fun timerThreadStart(finalMillis: Long){
        if (!isPomoRunning) { // This is so clicking start and resume repeatedly does not result in giving exp more than once
            isPomoRunning = true

            // Play Background Music
            intentBGMService.putExtra("SIGNAL_KEY", "start".toString())
            startService(intentBGMService)

            // ----- Change text to pause
            petScreenBinding.timerBtn1.text = resources.getString(R.string.pause)

            timerThread = object : CountDownTimer(finalMillis, 1000) {
                var hourMinSec = arrayOf(0L, 0L, 0L)

                override fun onTick(millisUntilFinished: Long) {
                    remainingMillisTimer = millisUntilFinished
                    hourMinSec = millisToTime(millisUntilFinished)
                    hourView.text = hourMinSec[0].toString()
                    minView.text = hourMinSec[1].toString().padStart(2, '0')
                    secView.text = hourMinSec[2].toString().padStart(2, '0')
                }

                override fun onFinish() {
                    val hourMinSec = millisToTime(originalMillisTimer)

                    // Update Experience and Level of Pet
                    updatePetExp(hourMinSec[0], hourMinSec[1], hourMinSec[2])

                    // ----- Set button text to start
                    petScreenBinding.timerBtn1.text = resources.getString(R.string.start)
                    isPomoRunning = false

                    initiatedPause = -1

                    intentBGMService.putExtra("SIGNAL_KEY", "finished")
                    stopService(intentBGMService)

                    // ----- Check if to continue Pomodoro
                    checkPomodoro()

                }
            }.start()
        }

    }



    // ----- Set countdown timer and updates the text in the timer
    private fun breakThreadStart(finalMillis: Long){
        if (!isBreakRunning) {
            isBreakRunning = true

            // ----- Change text to pause
            petScreenBinding.timerBtn1.text = resources.getString(R.string.pause)

            timerThread = object : CountDownTimer(finalMillis, 1000) {
                var hourMinSec = arrayOf(0L, 0L, 0L)
                override fun onTick(millisUntilFinished: Long) {
                    remainingMillisTimer = millisUntilFinished
                    hourMinSec = millisToTime(millisUntilFinished)
                    hourView.text = hourMinSec[0].toString()
                    minView.text = hourMinSec[1].toString().padStart(2, '0')
                    secView.text = hourMinSec[2].toString().padStart(2, '0')


                }

                override fun onFinish() {

                    initiatedPause = -1

                    // ----- Set button text to start
                    petScreenBinding.timerBtn1.text = resources.getString(R.string.start)
                    isBreakRunning = false
                    changeUIBreakToTimer()
                    timerThreadStart(originalMillisTimer)


                }
            }.start()
        }

    }

    private fun makeLevelUpUI()
    {
        if (!isLevelUpUIExists) {
            isLevelUpUIExists = true
            levelUpTextView = TextView(this)
            levelUpTextView.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0f
                )
            )
            levelUpTextView.setBackgroundColor(Color.parseColor("#E4E0EE"))
            levelUpTextView.gravity = Gravity.CENTER
            levelUpTextView.setPadding(5, 5, 5, 5)
            levelUpTextView.text = resources.getString(R.string.tap_here_to_level_up)
            levelUpTextView.setTextColor(Color.parseColor("#3C266F"))
            levelUpTextView.setTypeface(null, Typeface.BOLD)

            levelUpTextView.setOnClickListener {

                // Ask if single or double level up
                val intent = Intent(applicationContext, PetLevelUpActivity::class.java)
                intent.putExtra(PET_NAME, petScreenBinding.txtPetName.text.toString())
                levelUpActivityLauncher.launch(intent)

            }
            petScreenBinding.layoutMainScreen.addView(levelUpTextView, 5)
        }

    }

    private fun removeLevelUpNotif()
    {
        isLevelUpUIExists = false
        petScreenBinding.layoutMainScreen.removeViewAt(5)
    }

    // ----- Only used in timerThreadStart, breakThreadStart already assumes that there is a Pomodoro afterwards
    private fun checkPomodoro()
    {
        // decrement pomodoro
        currentPomodoro--

        // if pomodoro > 0, start break
        if (currentPomodoro > 0) {
            changeUITimerToBreak()
            breakThreadStart(settingsBreakDuration * 60000L)
        }
        // else, end
        else
            changeTimerToEditText()


    }

    private fun changeUITimerToBreak()
    {
        petScreenBinding.txtSemicolon1.setTextColor(Color.parseColor("#E4E0EE"))
        petScreenBinding.txtSemicolon2.setTextColor(Color.parseColor("#E4E0EE"))
        hourView.setTextColor(Color.parseColor("#E4E0EE"))
        minView.setTextColor(Color.parseColor("#E4E0EE"))
        secView.setTextColor(Color.parseColor("#E4E0EE"))
        petScreenBinding.layoutTimer.setBackgroundResource(R.drawable.rectangle_holder_color_inverted)
    }

    private fun changeUIBreakToTimer()
    {
        petScreenBinding.txtSemicolon1.setTextColor(Color.parseColor("#48444E"))
        petScreenBinding.txtSemicolon2.setTextColor(Color.parseColor("#48444E"))
        hourView.setTextColor(Color.parseColor("#48444E"))
        minView.setTextColor(Color.parseColor("#48444E"))
        secView.setTextColor(Color.parseColor("#48444E"))

        petScreenBinding.layoutTimer.setBackgroundResource(R.drawable.rectangle_holder)
    }

    private fun setTo0IfEmpty(string: String): String
    {
        if (string.isEmpty())
            return "0"

        return string
    }

    // ----- UI related stuff, nothing related to starting the timer
    private fun setupTimer()
    {
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

            if (petScreenBinding.timerBtn1.text.toString() == resources.getString(R.string.start)) {
                var getHour = findViewById<EditText>(timerIds[0]).text.toString()
                var getMin = findViewById<EditText>(timerIds[1]).text.toString()
                var getSec = findViewById<EditText>(timerIds[2]).text.toString()

                // helper function so it's less cumbersome to input for hour, min, and sec :)
                getHour = setTo0IfEmpty(getHour)
                getMin = setTo0IfEmpty(getMin)
                getSec = setTo0IfEmpty(getSec)

                // 00:00:00
                if (getHour.toInt() == 0 && getMin.toInt() == 0 && getSec.toInt() == 0)
                    Toast.makeText(this, "Cannot set timer to 0h 0m 0s.", Toast.LENGTH_SHORT).show()
                else if (getMin.toInt() < 0 || getMin.toInt() > 59)
                    Toast.makeText(this, "Minutes must be in range of 0 to 59.", Toast.LENGTH_SHORT)
                        .show()
                else if (getSec.toInt() < 0 || getSec.toInt() > 59)
                    Toast.makeText(this, "Seconds must be in range of 0 to 59.", Toast.LENGTH_SHORT)
                        .show()
                // start timer
                else
                {
                    if (!isPomoRunning) {
                        startTimerWithUI(getHour, getMin, getSec)

                        // ----- Used to calculate time lapsed when pausing
                        originalMillisTimer = timeToMillis(getHour.toInt(), getMin.toInt(), getSec.toInt())
                    }

                }
            }
            else if (petScreenBinding.timerBtn1.text.toString() == resources.getString(R.string.resume)){
                if (initiatedPause == 0)
                    timerThreadStart(remainingMillisTimer)
                else if (initiatedPause == 1)
                    breakThreadStart(remainingMillisTimer)

            }
            else if (petScreenBinding.timerBtn1.text.toString() == resources.getString(R.string.pause)) {
                pauseTimer()
            }


        }
    }


    private fun startTimerWithUI(getHour: String, getMin: String, getSec: String)
    {
        // ----- This part deletes edit texts [inputs] and replaces it with the text view of the timer
        // NOTE: we don't store the ids here, only in cancelTimer when editTexts are involved
        // (aka we need to get their inputs)

        // ----- Settings cannot be changed while a pomodoro is ongoing, this just means that these settings
        // will only apply on the next time user initiates a new timer
        currentPomodoro = settingsPomodoro
        currentBreakDuration = settingsBreakDuration

        // make inputBox (hour) to text view
        petScreenBinding.layoutTimer.removeViewAt(0)
        hourView = TextView(this)
        hourView.text = getHour
        hourView.textSize = 28f
        hourView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        hourView.textAlignment = TEXT_ALIGNMENT_CENTER
        hourView.setTypeface(null, Typeface.BOLD)
        petScreenBinding.layoutTimer.addView(hourView, 0)

        // make inputBox (minute) to text view
        petScreenBinding.layoutTimer.removeViewAt(2)
        minView = TextView(this)
        minView.text = getMin.padStart(2, '0')
        minView.textSize = 28f
        minView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        minView.textAlignment = TEXT_ALIGNMENT_CENTER
        minView.setTypeface(null, Typeface.BOLD)
        petScreenBinding.layoutTimer.addView(minView, 2)

        // make inputBox (seconds) to text view
        petScreenBinding.layoutTimer.removeViewAt(4)
        secView = TextView(this)
        secView.text = getSec.padStart(2, '0')
        secView.textSize = 28f
        secView.setLayoutParams(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.5f
            )
        )
        secView.textAlignment = TEXT_ALIGNMENT_CENTER
        secView.setTypeface(null, Typeface.BOLD)
        petScreenBinding.layoutTimer.addView(secView, 4)

        // ----- Timer thread starts
        timerThreadStart(timeToMillis(getHour.toInt(), getMin.toInt(), getSec.toInt()))


        // ----- Enable cancel button
        petScreenBinding.timerBtn0.isEnabled = true


    }

    private fun pauseTimer()
    {
        // ----- Cancel the timer thread
        if (isPomoRunning) {
            timerThread?.cancel()
            isPomoRunning = false

            initiatedPause = 0

            intentBGMService.putExtra("SIGNAL_KEY", "pause")
            startService(intentBGMService)

            // ----- Change text to resume
            petScreenBinding.timerBtn1.text = resources.getString(R.string.resume)
        }
        else if (isBreakRunning) {
            timerThread?.cancel()
            isBreakRunning = false

            initiatedPause = 1

            // ----- Change text to resume
            petScreenBinding.timerBtn1.text = resources.getString(R.string.resume)
        }


    }

    private fun cancelTimer(petScreenBinding: ActivityPetScreenBinding)
    {
        val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
        builder.setTitle("Cancel Timer?")

        builder.setMessage("Are you sure you want to cancel the current pomodoro timer?\n\n No EXP will be earned if a pomodoro timer is running.")
            // Show "Yes No" in that sequence rather than "No Yes"
            .setNegativeButton("Yes") { dialog, which ->

                hideSystemBars()

                initiatedPause = -1

                // ----- Cancel the timer thread
                if (isPomoRunning) {

                    intentBGMService.putExtra("SIGNAL_KEY", "finished")
                    stopService(intentBGMService)

                    timerThread?.cancel()
                    isPomoRunning = false
                }
                else if (isBreakRunning) {

                    intentBGMService.putExtra("SIGNAL_KEY", "finished")
                    stopService(intentBGMService)

                    timerThread?.cancel()
                    isBreakRunning = false
                }

                changeTimerToEditText()
                changeUIBreakToTimer()

                // ----- Disable start button and enable cancel button
                petScreenBinding.timerBtn0.isEnabled = false
                petScreenBinding.timerBtn1.isEnabled = true

                petScreenBinding.timerBtn1.text = resources.getString(R.string.start)

            }
            .setPositiveButton("No") { dialog, which ->
                hideSystemBars()
                dialog.dismiss()
            }
            .show()

    }

    private fun timeToMillis(hour: Int, min: Int, sec: Int): Long{
        return (hour * 3600000L) + (min * 60000L) + (sec * 1000L)
    }

    private fun millisToTime(millis: Long): Array<Long>{

        var hourMinSec = arrayOf(0L, 0L, 0L)
        hourMinSec[0] = millis / 3600000
        hourMinSec[1] = (millis % 3600000) / 60000
        hourMinSec[2] = (millis % 60000) / 1000

        return hourMinSec
    }

    // ----- Just a thread to make the pet move/animate
    private fun petAnimationStart()
    {
        animationDrawable = petScreenBinding.imgPet.drawable as AnimationDrawable

        // run pet animation
        handler.post{
            animationDrawable.start()
        }


    }

    private fun petAnimationStopAndStartNewLooper()
    {
        // stop animation
        handlerThread.quit()

        // because we quit the thread, the looper does not exist anymore
        // we need to instantiate a new one
        handlerThread = HandlerThread("AnimationThread").apply {
            start()
        }
        handler = Handler(handlerThread.looper)
    }

    // ----- Set the right animation for current pet and evolution
    private fun petTypeSet(iv: ImageView, petType: Int, evol: Int)
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

    private fun initRestorePetInfo(){
        // Retrieve Information
        currentPet = pomoDBHelper.getPet()[0]

        // Change details on screen to display current progress of the pet
        //petScreenBinding.imgPet.
        petScreenBinding.txtPetName.text = currentPet.pet_name // Pet Name
        petScreenBinding.txtLevel.text = "Level " + currentPet.pet_level.toString() // Pet Level

        // Experience Bar
        // Note: Always set the Max before the Progress. It matters.
        petScreenBinding.progressbarExp.max = currentPet.pet_max_exp // Maximum Exp to level up
        petScreenBinding.progressbarExp.progress = currentPet.pet_exp // Current Exp

        // level up overflow
        this.remainingExp = currentPet.remaining_exp.toDouble()
        this.extractedLvl = currentPet.extracted_lvl

        if (currentPet.is_level_up == 1) {
            petScreenBinding.progressbarExp.progress =  petScreenBinding.progressbarExp.max
            makeLevelUpUI()
        }

        // DEBUG for exp
        Log.d("CurrentPetExp", currentPet.pet_exp.toString())

    }



    private fun calculateIfLevelUp(earnedExp: Double, curExp: Int, maxExp: Int, curLvl: CharSequence)
    {
        // If earned exp is over the maximum experience points, level up pet and add the remaining earned exp
        if ( (earnedExp + curExp.toLong()) >= maxExp.toLong() ){
            Toast.makeText(this, "Congratulations! ${currentPet.pet_name} is ready to level up.", Toast.LENGTH_LONG).show()

            // Compute for remaining exp
            // Note: Absolutevalue property is used to make sure that the remaining exp is always a positive value
            val remainingExp = (maxExp.toLong() - (earnedExp + curExp.toLong())).absoluteValue
            this.remainingExp += remainingExp
            currentPet.remaining_exp = remainingExp.toInt()

            // Since curLvl is a string, find the numbers in the string then use the number
            val extractedLvl = curLvl.filter { it.isDigit() }.toString()
            this.extractedLvl = extractedLvl
            currentPet.extracted_lvl = extractedLvl

            petScreenBinding.progressbarExp.progress =  petScreenBinding.progressbarExp.max

            // pause timer
            pauseTimer()

            makeLevelUpUI()

            pomoDBHelper.setPetToLevelUp(currentPet)

            // Ask if single or double level up
            val intent = Intent(applicationContext, PetLevelUpActivity::class.java)
            intent.putExtra(PET_NAME, petScreenBinding.txtPetName.text.toString())
            levelUpActivityLauncher.launch(intent)

        } else {
            // Add Earned Exp to Current Exp
            val exp = (earnedExp + curExp).roundToInt()
            petScreenBinding.progressbarExp.progress = exp
            currentPet.pet_exp = exp

        }

        pomoDBHelper.savePetExp(currentPet)
    }

    fun updatePetExp(hour: Long, min: Long, seconds: Long){
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

        Log.d("expGiver", "Gave exp")

        val curLvl = petScreenBinding.txtLevel.text
        val curExp = petScreenBinding.progressbarExp.progress
        val maxExp = petScreenBinding.progressbarExp.max
        var earnedExp = 0.0

        if (hour != 0.toLong()) {
            // Compute Exp
            earnedExp = ((hour * 3600) * 0.5)


        } else if (min != 0.toLong()) {
            // Compute Exp
            earnedExp = ((min * 60) * 0.5)


        } else if (seconds != 0.toLong()) {
            // Compute Exp
            earnedExp = seconds * 0.5

        }

        calculateIfLevelUp(earnedExp, curExp, maxExp, curLvl)


        Toast.makeText(this, "Earned $earnedExp Exp Points!", Toast.LENGTH_LONG).show()

    }

    fun loadSettings()
    {
        val sharedPreferences = getSharedPreferences(FILE_SETTINGS, MODE_PRIVATE)

        settingsPomodoro = SettingsActivity.pomodoro_set_array[sharedPreferences.getInt(POMODOROS_SETTINGS, 0)]
        settingsBreakDuration = SettingsActivity.break_duration_array[sharedPreferences.getInt(POMO_BREAK_DURATION_SETTINGS, 2)]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this.intentBGMService = Intent(this@PetScreenActivity, BGMService::class.java)

        // ViewBind activity_pet_screen.xml
        val petScreenBinding: ActivityPetScreenBinding = ActivityPetScreenBinding.inflate(layoutInflater)

        // Initialize Global Variables
        this.petScreenBinding = petScreenBinding

        // Change the scene
        setContentView(petScreenBinding.root)

        /* ================
              LOAD DATA
          ================
        */

        pomoDBHelper = PomoDBHelper.getInstance(this@PetScreenActivity)!!
        initRestorePetInfo()

        loadSettings()



        // Display a toast welcoming the user
        Toast.makeText(this, "Welcome to PomoPet, ${currentPet.username}!", Toast.LENGTH_SHORT).show()

        // Test Activity View Exercise (Delete this after)
        //val viewExerciseTemplateIntentActivity = Intent(applicationContext, ViewExerciseActivity::class.java)


        // ----- Set the correct animation for the pet
        petTypeSet(petScreenBinding.imgPet, currentPet.pet_type, currentPet.pet_evol)

        // ----- Run animation

        petAnimationStart()

        // ----- Set values for pet details
        petScreenBinding.txtUsername.text = currentPet.username



        /* ================
                LISTENERS
           ================
         */

        // ----- Timer UI setups
        setupTimer() // UI and listener setups

        // ----- Pet Archive Button
        petScreenBinding.petArchiveLl.setOnClickListener{
            val petArchiveActivity = Intent(applicationContext, PetArchiveActivity::class.java)
            this.startActivity(petArchiveActivity)
            //this.startActivity(viewExerciseTemplateIntentActivity);
        }

        // ----- Pet Help Button
        petScreenBinding.helpBtn.setOnClickListener{

            val inflater = LayoutInflater.from(this)
            val viewInitialized = inflater.inflate(R.layout.builder_pomopet_help, null)

            val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
            builder.setTitle("PomoPet Help")
            builder.setView(viewInitialized)

            val btn_help_evolution = viewInitialized.findViewById<Button>(R.id.btn_help_evolution)
            val btn_help_expcalcu = viewInitialized.findViewById<Button>(R.id.btn_help_expcalcu)

            btn_help_evolution.setOnClickListener{
                val newDialog1 = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
                newDialog1.setTitle("Evolution Help")
                val dialogMessage1 = "This game calculates experience points (EXP) for your pet based on time spent during activities such as Pomodoro sessions. Here's how it works:<br><br>" +
                "<b>Maximum Experience per Level</b><br>" +
                        "Level 1: 1,000 EXP<br>" +
                        "Level 2: 2,000 EXP<br>" +
                        "Level 3: 3,000 EXP<br>" +
                        "Level 4: 4,000 EXP<br>" +
                        "Level N: N * 1000 EXP (i.e., the maximum EXP for a given level is the level number multiplied by 1,000).<br><br>" +
                        "<b>Evolutions</b><br>" +
                        "Level 10: 1st Evolution<br>" +
                        "Level 20: 2nd Evolution"

                val styledDialogMessage1 = HtmlCompat.fromHtml(dialogMessage1, HtmlCompat.FROM_HTML_MODE_LEGACY)
                newDialog1.setMessage(styledDialogMessage1)
                newDialog1.setPositiveButton(android.R.string.ok){dialog, which -> }
                newDialog1.show()


            }

            btn_help_expcalcu.setOnClickListener{
                val newDialog2 = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
                newDialog2.setTitle("EXP Calculation")
                val dialogMessage2 =
                    "<b>Experience Computation</b><br>" +
                    "Per second: You earn 0.5 EXP for each second of activity.<br><br>" +
                    "<b>Example Calculation</b><br>" +
                    "A 1-hour Pomodoro session (3,600 seconds) would earn you:<br>" +
                    "3,600 × 0.5 = 1,800 EXP<br>" +
                    "This is enough to level up from Level 1 to Level 2.<br><br>" +
                    "<b>EXP Calculation Based on Time</b><br>" +
                    "[1] If the time is given in hours, the system will compute the total EXP based on the number of hours.<br>" +
                    "[2] If the time is given in minutes, the system will compute EXP based on minutes.<br>" +
                    "[3] If the time is given in seconds, the system will compute EXP based on the exact number of seconds.<br><br>" +
                    "You can use this system to track your pet's progress, level it up, and unlock evolutions!"


                val styledDialogMessage2 = HtmlCompat.fromHtml(dialogMessage2, HtmlCompat.FROM_HTML_MODE_LEGACY)
                newDialog2.setMessage(styledDialogMessage2)
                newDialog2.setPositiveButton(android.R.string.ok){dialog, which -> }
                newDialog2.show()
            }

            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                //Toast.makeText(applicationContext, android.R.string.ok, Toast.LENGTH_SHORT).show()
            }
            builder.show()



        }

        // Inventory screen
        petScreenBinding.petInventoryLl.setOnClickListener{
            val intent = Intent(this, InventoryActivity::class.java)
            intent.putExtra(PET_EVOL, currentPet.pet_evol)
            intent.putExtra(PET_TYPE, currentPet.pet_type)
            intent.putExtra(PET_NAME, currentPet.pet_name)
            this.startActivity(intent)

        }

        // Settings screen
        petScreenBinding.settingsBtn.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            settingsActivityLauncher.launch(intent)
        }

        petScreenBinding.exerciseLl.setOnClickListener{
            val intent = Intent(this, ViewExerciseActivity::class.java)
            this.startActivity(intent)
        }




    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }


    // ----- This is to stop the threads prior to finishing the activity
    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit()
        handler.removeCallbacksAndMessages(null)


        stopService(intentBGMService)
        timerThread?.cancel()
    }
}