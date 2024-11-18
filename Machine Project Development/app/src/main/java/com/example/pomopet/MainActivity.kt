package com.example.pomopet

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pomopet.databinding.ActivityTitleScreenBinding


/* This is where the whole application starts */
class MainActivity : AppCompatActivity() {
    private lateinit var pomoDBHelper: PomoDBHelper

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


        // Start application by going to the Title Screen
        // ViewBind activity_title_screen.xml
        val actTitleScreenBind: ActivityTitleScreenBinding = ActivityTitleScreenBinding.inflate(layoutInflater)


        // Change the scene
        setContentView(actTitleScreenBind.root)

        // Change to Register Screen if user tapped anywhere in the screen
        actTitleScreenBind.layoutTitleScreenCl.setOnClickListener{

            pomoDBHelper = PomoDBHelper.getInstance(this@MainActivity)!!

            val hasRegistered = pomoDBHelper.getPet().isNotEmpty()

            // If new user, change to register screen. Else, go to activity main directly.
            if (!hasRegistered)
            {
                // Make intent of the next activity (Make sure to also include the intent in AndroidManifext.xml
                val registerIntent = Intent(applicationContext, RegisterActivity::class.java)
                // Go to next activity
                this.startActivity(registerIntent)
            }

            // existing user
            else {
                val mainPetScreenIntent = Intent(applicationContext, PetScreenActivity::class.java)
                this.startActivity(mainPetScreenIntent);
            }

            // Destroys this activity
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }


}