package com.example.pomopet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

//This file is the main file for the viewing of available exercises
class ViewExerciseActivity : AppCompatActivity() {

    //Initialize ArrayList
    private val dataModel: ArrayList<ViewExerDataModel> = ExerciseDataSet.loadData()

    //Recycler View Reference
    private lateinit var viewExerRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_exercise)

        //do we have to make use of viewbinding? - mark
        this.viewExerRecyclerView = findViewById(R.id.exercises_rv)
        this.viewExerRecyclerView.adapter = ViewExerciseAdapter(this.dataModel)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(viewExerRecyclerView)

        this.viewExerRecyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)
    }
}