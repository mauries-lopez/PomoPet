package com.example.pomopet

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
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
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.example.pomopet.databinding.ActivityNewEggBinding

class NewEggActivity : AppCompatActivity() {

    private lateinit var pomoDBHelper: PomoDBHelper
    private lateinit var animationDrawablePet: AnimationDrawable
    private lateinit var animationDrawableHand: AnimationDrawable

    private var handlerThread = HandlerThread("AnimationThread").apply {start()}
    private var handler = Handler(handlerThread.looper)


    private fun hideSystemBars() {
        val controller = WindowInsetsControllerCompat(
            window, window.decorView
        )

        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun instantiateRandomPetImageView(petChosen : Int): ImageView {
        val petHatchedImage = ImageView(this)
        val linearLayoutParams = LinearLayout.LayoutParams(
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

        return petHatchedImage
    }
    private fun instantiateCongratsText(): TextView {
        // ----- More dynamic adding of views
        val congratsText = TextView(this)
        congratsText.text = resources.getText(R.string.congrats)
        val linearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0f
        )
        congratsText.setLayoutParams(linearLayoutParams)
        congratsText.textAlignment = TEXT_ALIGNMENT_CENTER
        congratsText.textSize = 20f
        congratsText.setTypeface(null, Typeface.BOLD)

        return congratsText
    }

    private fun instantiateNameYourPetText():TextView {
        val namePetText = TextView(this)
        namePetText.text = resources.getText(R.string.name_your_pet)
        val linearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0f
        )
        namePetText.setLayoutParams(linearLayoutParams)
        namePetText.textAlignment = TEXT_ALIGNMENT_CENTER
        namePetText.textSize = 16f
        return namePetText
    }

    private fun instantiatePetNameEditText(): EditText{
        val petNameEditText = EditText(this)
        petNameEditText.hint = "Name"
        petNameEditText.setEms(10)
        petNameEditText.width = LinearLayout.LayoutParams.WRAP_CONTENT
        petNameEditText.height = LinearLayout.LayoutParams.WRAP_CONTENT
        petNameEditText.inputType = InputType.TYPE_CLASS_TEXT
        petNameEditText.textSize = 24f
        petNameEditText.textAlignment = TEXT_ALIGNMENT_CENTER
        return petNameEditText
    }
    private fun instantiatePetNameSubmitButton(): Button{
        val petNameSubmitButton = Button(this)
        petNameSubmitButton.gravity = Gravity.CENTER
        petNameSubmitButton.text = resources.getText(R.string.submit)
        petNameSubmitButton.textSize = 20f
        petNameSubmitButton.setTextColor(Color.parseColor("#FFFFFF"))
        petNameSubmitButton.setBackgroundColor(Color.parseColor("#664FA3"))
        petNameSubmitButton.setTypeface(null, Typeface.BOLD)
        val linearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0f
        )
        linearLayoutParams.topMargin = 50
        linearLayoutParams.bottomMargin = 50
        petNameSubmitButton.setLayoutParams(linearLayoutParams)
        return petNameSubmitButton
    }

    // ----- DEBUG for level/exp related purposes

    private fun saveDetails(username: String, petName: String, petChosen: Int) {

        val newPomoPet = PomoModel(
            username,
            petName,
            petChosen,
            1,
            1,
            0,
            1000,
        )

        pomoDBHelper = PomoDBHelper.getInstance(this@NewEggActivity)!!
        pomoDBHelper.insertNewPet(newPomoPet)

    }


    private fun addViewsAfterHatchedEgg(actNewEggActivity: ActivityNewEggBinding )
    {
        // Randomize pet
        val petChosen = (0..2).random()

        // ----- TLDR: Adding an ImageView of our pet based on the randomized value
        val petHatchedImage = instantiateRandomPetImageView(petChosen)
        actNewEggActivity.layoutHolder.addView(petHatchedImage)
        animationDrawablePet = petHatchedImage.drawable as AnimationDrawable

        handler.post{
            animationDrawablePet.start()
        }

        actNewEggActivity.layoutHolder.addView(instantiateCongratsText())
        actNewEggActivity.layoutHolder.addView(instantiateNameYourPetText())

        val petNameEditText = instantiatePetNameEditText()
        actNewEggActivity.layoutHolder.addView(petNameEditText)

        val petNameSubmitButton = instantiatePetNameSubmitButton()

        // ----- We also add a listener for the button to move to the main pet screen
        petNameSubmitButton.setOnClickListener(){
            if (petNameEditText.text.toString().isEmpty())
                Toast.makeText(this, "Empty pet name.", Toast.LENGTH_SHORT).show()
            else
            {
                saveDetails(intent.getStringExtra(PetScreenActivity.USERNAME).toString(), petNameEditText.text.toString(), petChosen)

                val toPetScreenIntent = Intent(this, PetScreenActivity::class.java)
                this.startActivity(toPetScreenIntent)
                this.finish()
            }
        }
        actNewEggActivity.layoutHolder.addView(petNameSubmitButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start application by going to the Title Screen
        // ViewBind activity_title_screen.xml
        val actNewEggActivity: ActivityNewEggBinding = ActivityNewEggBinding.inflate(layoutInflater)

        // Change the scene
        setContentView(actNewEggActivity.root)

        // ----- view is premade, we don't have to do much, just animate the thing
        // ----- Object to animate
        animationDrawableHand = actNewEggActivity.imgHand.drawable as AnimationDrawable

        // ----- Run hand animation
        handler.post{
            animationDrawableHand.start()
        }


        // ----- Tap anywhere to hatch egg
        actNewEggActivity.layoutHatchEgg.setOnClickListener{

            // stop hand animation
            handlerThread.quit()

            // because we quit the thread, the looper does not exist anymore
            // we need to instantiate a new one
            handlerThread = HandlerThread("AnimationThread").apply {
                start()
            }
            handler = Handler(handlerThread.looper)


            // without this code, user will be able to repeatedly click on the screen,
            // generating different pets and potential memory overloading
            actNewEggActivity.layoutHatchEgg.setOnClickListener(null)

            // remove egg, hand, and text; we will dynamically add views to reveal the pet
            actNewEggActivity.layoutHolder.removeAllViews()

            // ----- add views after hatching the egg
            addViewsAfterHatchedEgg(actNewEggActivity)



        }


    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }


    // ----- Stop animation threads
    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit()
        handler.removeCallbacksAndMessages(null)
    }
}