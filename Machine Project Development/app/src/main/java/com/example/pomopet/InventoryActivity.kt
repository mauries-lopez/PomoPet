package com.example.pomopet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pomopet.databinding.ActivityInventoryBinding

class InventoryActivity : AppCompatActivity() {

    var item1Description = -1
    var item2Description = -1
    var item3Description = -1
    var item1Image = -1
    var item2Image = -1
    var item3Image = -1


    // ----- Just an organized way to set inventories
    fun setRedPetInventory(evol: Int)
    {
        when(evol){
            2-> {
                item1Image = R.drawable.item_red1
                item1Description = R.string.red_evol1_item
                item2Image = R.drawable.item_red2
                item2Description = R.string.red_evol2_item
            }
            3-> {
                item1Image = R.drawable.item_red1
                item1Description = R.string.red_evol1_item
                item2Image = R.drawable.item_red2
                item2Description = R.string.red_evol2_item
                item3Image = R.drawable.item_red3
                item3Description = R.string.red_evol3_item
            }
            else->
            {
                item1Image = R.drawable.item_red1
                item1Description = R.string.red_evol1_item
            }

        }
    }
    fun setPurplePetInventory(evol: Int)
    {
        when(evol){
            2-> {
                item1Image = R.drawable.item_purple1
                item1Description = R.string.purple_evol1_item
                item2Image = R.drawable.item_purple2
                item2Description = R.string.purple_evol2_item
            }
            3-> {
                item1Image = R.drawable.item_purple1
                item1Description = R.string.purple_evol1_item
                item2Image = R.drawable.item_purple2
                item2Description = R.string.purple_evol2_item
                item3Image = R.drawable.item_purple3
                item3Description = R.string.purple_evol3_item
            }
            else->
            {
                item1Image = R.drawable.item_purple1
                item1Description = R.string.purple_evol1_item
            }
        }
    }

    fun setOrangePetInventory(evol: Int)
    {
        when(evol){
            2-> {
                item1Image = R.drawable.item_orange1
                item1Description = R.string.orange_evol1_item
                item2Image = R.drawable.item_orange2
                item2Description = R.string.orange_evol2_item
            }
            3-> {
                item1Image = R.drawable.item_orange1
                item1Description = R.string.orange_evol1_item
                item2Image = R.drawable.item_orange2
                item2Description = R.string.orange_evol2_item
                item3Image = R.drawable.item_orange3
                item3Description = R.string.orange_evol3_item
            }
            else->
            {
                item1Image = R.drawable.item_orange1
                item1Description = R.string.orange_evol1_item
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val inventoryActivity: ActivityInventoryBinding = ActivityInventoryBinding.inflate(layoutInflater)
        // Change the scene
        setContentView(inventoryActivity.root)

        val name = intent.getStringExtra(PetScreenActivity.PET_NAME).toString()
        val type = intent.getIntExtra(PetScreenActivity.PET_TYPE, -1)
        val evol = intent.getIntExtra(PetScreenActivity.PET_EVOL, -1)

        // default values
        item1Image = R.drawable.item_questionmark
        item2Image = R.drawable.item_questionmark
        item3Image = R.drawable.item_questionmark

        item1Description = R.string.locked_item
        item2Description = R.string.locked_item
        item3Description = R.string.locked_item


        when(type)
        {
            PetScreenActivity.RED_PET->setRedPetInventory(evol)
            PetScreenActivity.PURPLE_PET->setPurplePetInventory(evol)
            PetScreenActivity.ORANGE_PET->setOrangePetInventory(evol)
        }

        inventoryActivity.imgItem1.setImageResource(item1Image)
        inventoryActivity.imgItem2.setImageResource(item2Image)
        inventoryActivity.imgItem3.setImageResource(item3Image)


        inventoryActivity.layoutInventorySlot1.setOnClickListener{
            inventoryActivity.imgItemSelected.setImageResource(item1Image)
            inventoryActivity.txtItemDesc.text = String.format(getString(item1Description), name)
        }
        inventoryActivity.layoutInventorySlot2.setOnClickListener{
            inventoryActivity.imgItemSelected.setImageResource(item2Image)
            inventoryActivity.txtItemDesc.text = String.format(getString(item2Description), name)
        }
        inventoryActivity.layoutInventorySlot3.setOnClickListener{
            inventoryActivity.imgItemSelected.setImageResource(item3Image)
            inventoryActivity.txtItemDesc.text = String.format(getString(item3Description), name)

        }

        inventoryActivity.btnInventoryback.setOnClickListener{
            finish()
        }



    }
}