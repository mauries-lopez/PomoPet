package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.material3.Card
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.core.view.get
import com.example.pomopet.databinding.ActivityPetArchiveBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PetArchiveActivity : AppCompatActivity() {

    val allDrawables = listOf(
        R.drawable.anim_evol1_orange,
        R.drawable.anim_evol1_purple,
        R.drawable.anim_evol1_red,
        R.drawable.anim_evol2_orange,
        R.drawable.anim_evol2_purple,
        R.drawable.anim_evol2_red,
        R.drawable.anim_evol3_orange,
        R.drawable.anim_evol3_purple,
        R.drawable.anim_evol3_red
    )


    companion object {
        val PET_ANIMATION = "PET_ANIMATION"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBind activity_pet_archive.xml
        val petArchiveBinding : ActivityPetArchiveBinding = ActivityPetArchiveBinding.inflate(layoutInflater)
        // Change the scene
        setContentView(petArchiveBinding.root)

        // If any of the card is click... Do something...
        petArchiveBinding.petArchiveIndex0Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[0])
            this.startActivity(intent)
        }

        petArchiveBinding.petArchiveIndex1Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[1])
            this.startActivity(intent)
        }

        petArchiveBinding.petArchiveIndex2Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[2])
            this.startActivity(intent)
        }

        petArchiveBinding.petArchiveIndex3Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[3])
            this.startActivity(intent)
        }

        petArchiveBinding.petArchiveIndex4Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[4])
            this.startActivity(intent)
        }

        petArchiveBinding.petArchiveIndex5Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[5])
            this.startActivity(intent)
        }

        petArchiveBinding.petArchiveIndex6Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[6])
            this.startActivity(intent)
        }
        petArchiveBinding.petArchiveIndex7Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[7])
            this.startActivity(intent)
        }
        petArchiveBinding.petArchiveIndex8Cv.getChildAt(0).setOnClickListener{
            val intent = Intent(this, ViewPetArchiveActivity::class.java)
            intent.putExtra(PET_ANIMATION, allDrawables[8])
            this.startActivity(intent)
        }





        // Back Button
        petArchiveBinding.backBtn.setOnClickListener{
            finish()
        }

    }

}