    package com.example.pomopet

    import android.view.LayoutInflater
    import android.webkit.WebView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.example.pomopet.databinding.ViewExerciseTemplateBinding
    import com.google.android.material.dialog.MaterialAlertDialogBuilder

    class ViewExerViewHolder(private val viewbinding: ViewExerciseTemplateBinding) : RecyclerView.ViewHolder(viewbinding.root) {
        fun linkData(viewExerDataModel: ViewExerDataModel) {


            viewbinding.exerName.text = viewExerDataModel.exerName
            viewbinding.exerIcon.setImageResource(viewExerDataModel.exerIcon)

            viewbinding.btnViewExercise.setOnClickListener{
                val inflater = LayoutInflater.from(viewbinding.root.context)
                val viewInitialized = inflater.inflate(R.layout.builder_exercise_video_desc, null)

                val builder = MaterialAlertDialogBuilder(viewbinding.root.context, R.style.MaterialAlertDialogStyle)
                builder.setTitle(viewExerDataModel.exerName)
                builder.setView(viewInitialized)
                val video_exer = viewInitialized.findViewById<WebView>(R.id.video_exer)
                val txt_help_exer = viewInitialized.findViewById<TextView>(R.id.txt_help_exer)

                video_exer.settings.javaScriptEnabled = true
                video_exer.loadData(viewExerDataModel.exerVid ?: "No Video", "text/html", "UTF-8")
                txt_help_exer.setText(viewExerDataModel.exerDesc ?: "No Description")

                builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    //Toast.makeText(applicationContext, android.R.string.ok, Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }


        }
    }