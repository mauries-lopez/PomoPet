package com.example.pomopet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//This file is the main file for the viewing of available exercises
class ViewExercise : AppCompatActivity() {

    //Initialize ArrayList
    private val dataModel: ArrayList<ViewExerDataModel> = ExerciseDataSet.loadData()

    //Recycler View Reference
    private lateinit var viewExerRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_exercise)

        //Added
        this.viewExerRecyclerView = findViewById(R.id.exercises_rv)
        this.viewExerRecyclerView.adapter = ViewExerciseAdapter(this.dataModel)
        this.viewExerRecyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)
    }
}