package com.example.pomopet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.pomopet.databinding.ViewExerciseTemplateBinding

//Getting a copy of the data to the adapter
class ViewExerciseAdapter(private val data: ArrayList<ViewExerDataModel>): Adapter<ViewExerViewHolder>(){

    //Creating the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewExerViewHolder {
        val itemViewBinding2 : ViewExerciseTemplateBinding =
            ViewExerciseTemplateBinding.inflate(LayoutInflater.from(parent.context))
        val myViewHolder = ViewExerViewHolder(itemViewBinding2)
        return myViewHolder
    }

    //Binding data to the ViewHolder
    override fun onBindViewHolder(holder: ViewExerViewHolder, position: Int) {
        holder.linkData(data[position])
    }

    //Specify ArrayList size
    override fun getItemCount(): Int {
        return data.size
    }
}