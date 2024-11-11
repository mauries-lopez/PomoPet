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

    constructor(username: String, pet_name: String,  pet_type: Int, pet_level: Int, pet_evol: Int, pet_exp: Int, pet_max_exp: Int){
        this.username = username
        this.pet_name = pet_name
        this.pet_type = pet_type
        this.pet_level = pet_level
        this.pet_evol = pet_evol
        this.pet_exp = pet_exp
        this.pet_max_exp = pet_max_exp
    }

    constructor(username: String, pet_name: String,  pet_type: Int, pet_level: Int, pet_evol: Int, pet_exp: Int, pet_max_exp: Int, id: Long){
        this.username = username
        this.pet_name = pet_name
        this.pet_type = pet_type
        this.pet_level = pet_level
        this.pet_evol = pet_evol
        this.pet_exp = pet_exp
        this.pet_max_exp = pet_max_exp
        this.id = id
    }

}