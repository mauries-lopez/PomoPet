package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    companion object {
        // Create static variable for the intent to use
        const val USERNAME = "USERNAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBind activity_register.xml
        val actActivityRegisterBind: ActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)

        // Change the scene
        setContentView(actActivityRegisterBind.root)

        // When user confirms name, do action
        actActivityRegisterBind.confirmNameBtn.setOnClickListener{

            val signName = actActivityRegisterBind.welcomeUserSignedTv.text
            // Check if name empty
            if ( signName.isEmpty() ){
                Toast.makeText(this, "Enter your username to continue.", Toast.LENGTH_SHORT).show()
            } else { // Else, proceed to PomoPet
                val pomopetMainActivityIntent = Intent(applicationContext, PetScreenActivity::class.java)
                // Put username to intent so the main activity can use it
                pomopetMainActivityIntent.putExtra(USERNAME, signName.toString())
                // Go to next activity
                this.startActivity(pomopetMainActivityIntent);
                // Destroys this activity
                this.finish()
            }
        }
    }
}