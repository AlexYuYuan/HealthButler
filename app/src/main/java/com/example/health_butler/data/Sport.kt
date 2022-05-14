package com.example.health_butler.data

class Sport(name: String, calorie: Int) {

    private val name: String
    private val calorie: Int

    init {
        this.name = name
        this.calorie = calorie
    }

    fun getName():String{
        return name
    }

    fun getCalorie():Int{
        return calorie
    }
}