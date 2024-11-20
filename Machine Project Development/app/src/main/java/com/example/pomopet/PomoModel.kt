package com.example.pomopet

class PomoModel {
    var id: Long = 0
    var username: String
        private set
    var pet_name: String
        private set
    var pet_type: Int
        private set
    var pet_level: Int
    var pet_evol: Int
    var pet_exp: Int
    var pet_max_exp: Int
    var is_level_up: Int
    var remaining_exp: Int
    var extracted_lvl: String

    constructor(username: String, pet_name: String,  pet_type: Int, pet_level: Int, pet_evol: Int, pet_exp: Int, pet_max_exp: Int){
        this.username = username
        this.pet_name = pet_name
        this.pet_type = pet_type
        this.pet_level = pet_level
        this.pet_evol = pet_evol
        this.pet_exp = pet_exp
        this.pet_max_exp = pet_max_exp
        this.is_level_up = 0
        this.remaining_exp = 0
        this.extracted_lvl = ""
    }

    constructor(username: String, pet_name: String,  pet_type: Int, pet_level: Int, pet_evol: Int, pet_exp: Int, pet_max_exp: Int, is_level_up: Int, remaining_exp: Int, extracted_lvl: String){
        this.username = username
        this.pet_name = pet_name
        this.pet_type = pet_type
        this.pet_level = pet_level
        this.pet_evol = pet_evol
        this.pet_exp = pet_exp
        this.pet_max_exp = pet_max_exp
        this.is_level_up = is_level_up
        this.remaining_exp = remaining_exp
        this.extracted_lvl = extracted_lvl
    }

    constructor(username: String, pet_name: String,  pet_type: Int, pet_level: Int, pet_evol: Int, pet_exp: Int, pet_max_exp: Int, is_level_up: Int, remaining_exp: Int, extracted_lvl: String, id: Long){
        this.username = username
        this.pet_name = pet_name
        this.pet_type = pet_type
        this.pet_level = pet_level
        this.pet_evol = pet_evol
        this.pet_exp = pet_exp
        this.pet_max_exp = pet_max_exp
        this.is_level_up = is_level_up
        this.remaining_exp = remaining_exp
        this.extracted_lvl = extracted_lvl
        this.id = id

    }

}