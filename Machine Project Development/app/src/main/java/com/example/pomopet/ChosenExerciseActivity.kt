package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.ChosenExerciseBinding

class ChosenExerciseActivity : AppCompatActivity() {

    //Initialize chosen record
    private lateinit var viewBinding : ChosenExerciseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ChosenExerciseBinding.inflate(layoutInflater) // Inflate the binding
        setContentView(viewBinding.root) // Set the content view to the binding's root

        // Initialize the ExerciseModel, ?: for handling null values
        val exerciseModel = ViewExerDataModel(
            exerName = intent.getStringExtra("EXER_NAME") ?: "Unknown Exercise",
            exerIcon = intent.getIntExtra("EXER_ICON", 0),
            exerVid = intent.getStringExtra("EXER_VID") ?: "No Video",
            exerDesc = intent.getStringExtra("EXER_DESC") ?: "No Description"
        )

        // Use view binding to set the data to the views
        viewBinding.chosenName.text = exerciseModel.exerName
        viewBinding.chosenIcon.setImageResource(exerciseModel.exerIcon)
        viewBinding.chosenDesc.text = exerciseModel.exerDesc
        viewBinding.chosenVid.settings.javaScriptEnabled = true
        viewBinding.chosenVid.loadData(exerciseModel.exerVid, "text/html", "UTF-8")

        // finish activity as PetScreenActivity never calls finish()
        viewBinding.backBtn.setOnClickListener {
            val intent = Intent()
            finish()
        }
    }
}