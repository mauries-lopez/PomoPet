package com.example.pomopet

import androidx.recyclerview.widget.RecyclerView
import com.example.pomopet.databinding.ViewExerciseTemplateBinding

class ViewExerViewHolder(private val viewbinding: ViewExerciseTemplateBinding) : RecyclerView.ViewHolder(viewbinding.root) {
    fun linkData(viewExerDataModel: ViewExerDataModel) {
        viewbinding.exerName.text = viewExerDataModel.exerName
        viewbinding.exerIcon.setImageResource(viewExerDataModel.exerIcon)
        viewbinding.exerDesc.text = viewExerDataModel.exerDesc

        // Set up WebView for YouTube video
        viewbinding.exerVid.settings.javaScriptEnabled = true

        // Initialize variable
        val exerVid = viewExerDataModel.exerVid

        // Load the URL
        viewbinding.exerVid.loadUrl(exerVid)
    }
}