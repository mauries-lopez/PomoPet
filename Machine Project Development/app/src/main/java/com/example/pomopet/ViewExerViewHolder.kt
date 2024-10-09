package com.example.pomopet

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class ViewExerViewHolder (itemView: View): ViewHolder(itemView) {

    //Initialization, di pa ako sure pano via ViewBinding - Mark
    private val exerName: TextView = itemView.findViewById(R.id.exerName)
    private val exerIcon: ImageView = itemView.findViewById(R.id.exerIcon)

    fun linkData(ViewExerDataModel: ViewExerDataModel){
        exerName.text = ViewExerDataModel.exerName
        exerIcon.setImageResource(ViewExerDataModel.exerIcon)
    }
}