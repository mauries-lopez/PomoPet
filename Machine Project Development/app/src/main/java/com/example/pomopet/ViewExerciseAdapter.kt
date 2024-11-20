package com.example.pomopet

import android.content.Intent
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

        /* Commenting this out, it loads ChosenExerciseActivity
        // Select random item from the dataset
        val randomItem = ExerciseDataSet.loadData().shuffled().first()

        // Pass selected random item
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChosenExerciseActivity::class.java)
            intent.putExtra("ITEM_NAME", randomItem.exerName)
            intent.putExtra("ITEM_ICON", randomItem.exerIcon)
            intent.putExtra("ITEM_VIDEO", randomItem.exerVid)
            intent.putExtra("ITEM_DESC", randomItem.exerDesc)
            holder.itemView.context.startActivity(intent)
        }

         */
    }

    //Specify ArrayList size
    override fun getItemCount(): Int {
        return data.size
    }
}