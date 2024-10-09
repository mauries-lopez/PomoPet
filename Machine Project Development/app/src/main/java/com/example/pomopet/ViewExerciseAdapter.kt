package com.example.pomopet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

//Getting a copy of the data to the adapter
class ViewExerciseAdapter(private val data: ArrayList<ViewExerDataModel>): Adapter<ViewExerViewHolder>(){

    //Creating the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewExerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.view_exercise_template, parent, false)
        return ViewExerViewHolder(view)
    }

    //Binding data to the ViewHolder
    override fun onBindViewHolder(holder: ViewExerViewHolder, position: Int) {
        holder.linkData(data.get(position))
        //Action needed to be done, so basically, upon clicking dapat move to specific exercise detail
        holder.itemView.setOnClickListener {
            val current = data.get(position).exerName
            //Insert logic here to redirect to specific exercise page
        }
    }

    //Specify ArrayList size
    override fun getItemCount(): Int {
        return data.size
    }
}