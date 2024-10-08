package com.example.pomopet

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
        val loggedUsername = intent.getStringExtra(RegisterActivity.USERNAME)

        // Display a toast welcoming the user
        Toast.makeText(this, "Welcome to PomoPet, $loggedUsername!", Toast.LENGTH_SHORT).show()
    }
}