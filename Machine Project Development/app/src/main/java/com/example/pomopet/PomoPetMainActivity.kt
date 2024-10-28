package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.PomopetMainActivityBinding

class PomoPetMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBind pomopet_main_activity.xml
        val pomoPetMainActivityBind: PomopetMainActivityBinding = PomopetMainActivityBinding.inflate(layoutInflater)
        // Change the scene
        setContentView(pomoPetMainActivityBind.root)
        // Get Username
        val loggedUsername = intent.getStringExtra(PetScreenActivity.USERNAME)
        // Display a toast welcoming the user
        Toast.makeText(this, "Welcome to PomoPet, $loggedUsername!", Toast.LENGTH_SHORT).show()

        // Lines 26 to 33 is for testing purposes. You may delete it or comment it out.
        // Test Activity View Exercise (Delete this after)
        val viewExerCiseTemplateIntentActivity = Intent(applicationContext, ViewExerciseActivity::class.java)

        // If timer start button is clicked, test recycler view
        pomoPetMainActivityBind.timerBtn1.setOnClickListener{
            this.startActivity(viewExerCiseTemplateIntentActivity);
        }
    }
}