package com.example.pomopet

//This file loads the information needed for the recycler view on the view exercises
class ExerciseDataSet {
    companion object {
        fun loadData(): ArrayList<ViewExerDataModel> {
            val data = ArrayList<ViewExerDataModel>()
            data.add(ViewExerDataModel("Jumping Jacks", R.drawable.jumping_jacks))
            data.add(ViewExerDataModel("Push Ups", R.drawable.push_ups))
            data.add(ViewExerDataModel("Squats", R.drawable.squats))
            data.add(ViewExerDataModel("Lunges", R.drawable.lunges))
            data.add(ViewExerDataModel("Burpees", R.drawable.burpees))
            data.add(ViewExerDataModel("Bicycle Crunches", R.drawable.bicycle_crunch))
            return data
        }
    }
}