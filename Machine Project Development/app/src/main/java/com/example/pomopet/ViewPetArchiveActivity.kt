package com.example.pomopet

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pomopet.databinding.ActivityViewPetArchiveBinding

class ViewPetArchiveActivity : AppCompatActivity() {

    lateinit var animationDrawable: AnimationDrawable

    var handlerThread = HandlerThread("AnimationThread").apply {start()}
    var handler = Handler(handlerThread.looper)

    // ----- Just a thread to make the pet move/animate
    fun petAnimationStart()
    {
        // run pet animation
        handler.post{
            animationDrawable.start()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewPetArchiveActivity : ActivityViewPetArchiveBinding = ActivityViewPetArchiveBinding.inflate(layoutInflater)
        val petToAnimate = intent.getIntExtra(PetArchiveActivity.PET_ANIMATION, -1)

        setContentView(viewPetArchiveActivity.root)

        viewPetArchiveActivity.imgPetArchiveView.setImageResource(petToAnimate)
        animationDrawable = viewPetArchiveActivity.imgPetArchiveView.drawable as AnimationDrawable
        petAnimationStart()

        viewPetArchiveActivity.btnBackArchive.setOnClickListener{
            finish()
        }

    }

    override fun onStop() {
        super.onStop()
        handlerThread.quit()
    }

}