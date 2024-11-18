package com.example.pomopet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pomopet.databinding.ActivityPetLevelUpBinding

class PetLevelUpActivity : AppCompatActivity() {

    private lateinit var petLevelUpActivityBind: ActivityPetLevelUpBinding

    companion object {
        const val RESULT_KEY = "RESULT_KEY"
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
        enableEdgeToEdge()

        // ViewBind activity_pet_level_up.xml
        petLevelUpActivityBind = ActivityPetLevelUpBinding.inflate(layoutInflater)
        // Change the scene
        setContentView(petLevelUpActivityBind.root)

        // Get Pet Name
        val petName = intent.getStringExtra(PetScreenActivity.PET_NAME)

        // Replace "%1$s" in Level Up Message
        val text = petLevelUpActivityBind.levelUpMsgTv.text.toString()
        val updatedText = String.format(text, petName)
        petLevelUpActivityBind.levelUpMsgTv.text = updatedText

        // Get Level Up Choice
        var scalar = 0 // Initialize scalar at the class level

        petLevelUpActivityBind.txtMsgXBtn.setOnClickListener{
            finish()
        }

        // Single Level Up
        petLevelUpActivityBind.levelUpOneBtn.setOnClickListener {
            Toast.makeText(this, "SINGLE LEVEL UP !!", Toast.LENGTH_SHORT).show()
            scalar = 1
            val returnIntent = Intent()
            returnIntent.putExtra(RESULT_KEY, scalar)
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        petLevelUpActivityBind.levelUpDoubleBtn.setOnClickListener {
            // Select a random item from the ExerciseDataSet
            val randomItem = ExerciseDataSet.loadData().shuffled().first()

            Toast.makeText(this, "DOUBLE LEVEL UP !!", Toast.LENGTH_SHORT).show()
            scalar = 2

            // Start ChosenExerciseActivity and pass the individual properties
            val intent = Intent(this, ChosenExerciseActivity::class.java)
            intent.putExtra("EXER_NAME", randomItem.exerName)
            intent.putExtra("EXER_ICON", randomItem.exerIcon)
            intent.putExtra("EXER_VID", randomItem.exerVid)
            intent.putExtra("EXER_DESC", randomItem.exerDesc)
            startActivity(intent)

            // Create return intent with the scalar value
            val returnIntent = Intent()
            returnIntent.putExtra(RESULT_KEY, scalar)
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

}
