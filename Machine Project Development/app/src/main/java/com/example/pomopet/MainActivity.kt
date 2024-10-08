package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.ActivityTitleScreenBinding


/* This is where the whole application starts */
class MainActivity : AppCompatActivity() {
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
            // TO DO (not now): If new user, change to register screen. Else, go to activity main directly.
            //Toast.makeText(this, "Going to Register Screen", Toast.LENGTH_SHORT).show()
            // Make intent of the next activity (Make sure to also include the intent in AndroidManifext.xml
            val registerIntent = Intent(applicationContext, RegisterActivity::class.java)
            // Go to next activity
            this.startActivity(registerIntent);
            // Destroys this activity
            this.finish()
        }

    }
}