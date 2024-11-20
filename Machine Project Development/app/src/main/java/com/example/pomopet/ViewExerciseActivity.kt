package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.pomopet.databinding.ActivityViewExerciseBinding

class ViewExerciseActivity : AppCompatActivity() {

    // Initialize ArrayList
    private val dataModel: ArrayList<ViewExerDataModel> = ExerciseDataSet.loadData()

    private lateinit var viewBinding : ActivityViewExerciseBinding

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
        viewBinding = ActivityViewExerciseBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.exercisesRv.adapter = ViewExerciseAdapter(dataModel)

        val helper: LinearSnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(viewBinding.exercisesRv)

        viewBinding.exercisesRv.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)

        viewBinding.floatBackBtn.setOnClickListener {
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

}
