package com.example.pomopet

import android.widget.ImageView

class PetModel(name: String, exp: Int, level: Int, imageIv: ImageView) {
    var name = name
        private set
    var exp = exp
        private set
    var level = level
        private set
    var imageIv = imageIv
        private set
}