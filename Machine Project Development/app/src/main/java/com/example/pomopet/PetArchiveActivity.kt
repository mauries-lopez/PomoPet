package com.example.pomopet

import android.os.Bundle
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.children
import com.example.pomopet.databinding.ActivityPetArchiveBinding


class PetArchiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBind activity_pet_archive.xml
        val petArchiveBinding : ActivityPetArchiveBinding = ActivityPetArchiveBinding.inflate(layoutInflater)
        // Change the scene
        setContentView(petArchiveBinding.root)

        // If any of the card is click... Do something...
        for (i in petArchiveBinding.petArchiveGl.children) {
            i.setOnClickListener{

            }
        }

        // Back Button
        petArchiveBinding.backBtn.setOnClickListener{
            finish()
        }

    }

}