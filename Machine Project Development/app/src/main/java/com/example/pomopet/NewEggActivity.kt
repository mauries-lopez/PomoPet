package com.example.pomopet

import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.Gravity
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.animation.Animation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.example.pomopet.databinding.ActivityNewEggBinding

class NewEggActivity : AppCompatActivity() {

    lateinit var animationDrawablePet: AnimationDrawable
    lateinit var animationDrawableHand: AnimationDrawable
    var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start application by going to the Title Screen
        // ViewBind activity_title_screen.xml
        val actNewEggActivity: ActivityNewEggBinding = ActivityNewEggBinding.inflate(layoutInflater)

        // Change the scene
        setContentView(actNewEggActivity.root)

        // ----- Object to animate
        animationDrawableHand = actNewEggActivity.imgHand.drawable as AnimationDrawable

        // ----- Run pet animation
        Thread {
            handler.post{
                animationDrawableHand.start()
            }
        }.start()

        // ----- Tap anywhere to hatch egg
        actNewEggActivity.layoutHatchEgg.setOnClickListener{

            // stop hand animation
            animationDrawableHand.stop()

            // without this code, user will be able to repeatedly click on the screen,
            // generating different pets and potential memory overloading
            actNewEggActivity.layoutHatchEgg.setOnClickListener(null)

            // remove egg, hand, and text; we will dynamically add views to reveal the pet
            actNewEggActivity.layoutHolder.removeAllViews()

            // Randomize pet
            val petChosen = (0..2).random()

            // ----- TLDR: Adding an ImageView of our pet based on the randomized value
            val petHatchedImage = ImageView(this);
            var linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                4f
            )
            linearLayoutParams.topMargin = 50
            petHatchedImage.setLayoutParams(linearLayoutParams)
            petHatchedImage.setPadding(200)
            when (petChosen){
                PetScreenActivity.RED_PET->petHatchedImage.setImageResource(R.drawable.anim_evol1_red)
                PetScreenActivity.PURPLE_PET->petHatchedImage.setImageResource(R.drawable.anim_evol1_purple)
                PetScreenActivity.ORANGE_PET->petHatchedImage.setImageResource(R.drawable.anim_evol1_orange)
            }
            actNewEggActivity.layoutHolder.addView(petHatchedImage)
            animationDrawablePet = petHatchedImage.drawable as AnimationDrawable


            // ----- Run pet animation
            Thread {
                handler.post{
                    animationDrawablePet.start()
                }
            }.start()

            // ----- More dynamic adding of views
            val congratsText = TextView(this)
            congratsText.text = resources.getText(R.string.congrats)
            linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0f
            )
            congratsText.setLayoutParams(linearLayoutParams)
            congratsText.textAlignment = TEXT_ALIGNMENT_CENTER
            congratsText.textSize = 20f
            congratsText.setTypeface(null, Typeface.BOLD)
            actNewEggActivity.layoutHolder.addView(congratsText)

            val namePetText = TextView(this)
            namePetText.text = resources.getText(R.string.name_your_pet)
            linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0f
            )
            namePetText.setLayoutParams(linearLayoutParams)
            namePetText.textAlignment = TEXT_ALIGNMENT_CENTER
            namePetText.textSize = 16f
            actNewEggActivity.layoutHolder.addView(namePetText)

            val petNameEditText = EditText(this)
            petNameEditText.hint = "Name"
            petNameEditText.setEms(10)
            petNameEditText.width = LinearLayout.LayoutParams.WRAP_CONTENT
            petNameEditText.height = LinearLayout.LayoutParams.WRAP_CONTENT
            petNameEditText.inputType = InputType.TYPE_CLASS_TEXT
            petNameEditText.textSize = 24f
            petNameEditText.textAlignment = TEXT_ALIGNMENT_CENTER
            actNewEggActivity.layoutHolder.addView(petNameEditText)

            val petNameSubmitButton = Button(this)
            petNameSubmitButton.gravity = Gravity.CENTER
            petNameSubmitButton.text = resources.getText(R.string.submit)
            petNameSubmitButton.textSize = 20f
            petNameSubmitButton.setTypeface(null, Typeface.BOLD)
            linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0f
            )
            linearLayoutParams.topMargin = 50
            linearLayoutParams.bottomMargin = 50
            petNameSubmitButton.setLayoutParams(linearLayoutParams)

            // ----- We also add a listener for the button to move to the main pet screen
            petNameSubmitButton.setOnClickListener(){
                if (petNameEditText.text.toString().isEmpty())
                    Toast.makeText(this, "Empty pet name.", Toast.LENGTH_SHORT).show()
                else
                {
                    // not sure if application context is needed here
                    val toPetScreenIntent = Intent(this, PetScreenActivity::class.java)
                    toPetScreenIntent.putExtra(RegisterActivity.USERNAME, intent.getStringExtra(RegisterActivity.USERNAME))
                    toPetScreenIntent.putExtra(PetScreenActivity.PET_NAME, petNameEditText.text.toString())
                    toPetScreenIntent.putExtra(PetScreenActivity.PET_TYPE, petChosen)
                    toPetScreenIntent.putExtra(PetScreenActivity.EVOL, 1)
                    this.startActivity(toPetScreenIntent)
                    this.finish()
                }
            }
            actNewEggActivity.layoutHolder.addView(petNameSubmitButton)

        }
    }
    // ----- Stop animation threads
    override fun onDestroy() {
        super.onDestroy()
        animationDrawablePet.stop()
        animationDrawableHand.stop()
        handler.removeCallbacksAndMessages(null)
    }
}