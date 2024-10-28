package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.pomopet.databinding.ActivityViewExerciseBinding

class ViewExerciseActivity : AppCompatActivity() {

    // Initialize ArrayList
    private val dataModel: ArrayList<ViewExerDataModel> = ExerciseDataSet.loadData()

    private lateinit var viewBinding : ActivityViewExerciseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityViewExerciseBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.exercisesRv.adapter = ViewExerciseAdapter(dataModel)

        val helper: LinearSnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(viewBinding.exercisesRv)

        viewBinding.exercisesRv.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)

        viewBinding.floatBackBtn.setOnClickListener {
            val intent = Intent()
            finish()
        }
    }
}
