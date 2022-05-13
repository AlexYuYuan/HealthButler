package com.example.health_butler

class Food(name: String, unit:String, calorie: Int, carbohydrate: Int, protein: Int, fat: Int) {
    private var name: String
    private val unit: String
    private var calorie: Int
    private var carbohydrate: Int
    private var protein: Int
    private var fat: Int
    private val type: String = "userDefined "

    init {
        this.name = name
        this.unit = unit
        this.calorie = calorie
        this.carbohydrate = carbohydrate
        this.protein = protein
        this.fat = fat
    }

    fun getName(): String{
        return name
    }

    fun getType(): String{
        return type
    }
    fun getUnit(): String{
        return unit
    }

    fun getCalorie(): Int{
        return calorie
    }

    fun getCarbohydrate(): Int{
        return carbohydrate
    }
    fun getProtein(): Int{
        return protein
    }
    fun getFat(): Int{
        return fat
    }
}