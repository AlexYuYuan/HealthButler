package com.example.health_butler.data

import java.text.SimpleDateFormat

enum class PERIOD(val value: Int){
    YEAR(0),
    MONTH(1),
    THREEMONTH(2),
    ALL(3);
    companion object {
        fun fromInt(value: Int) = PERIOD.values().first { it.value == value }
    }
}
enum class TABLE(val value: Int){
    DIET(0),
    SPORT(1),
    DRINK(2);
    companion object {
        fun fromInt(value: Int) = TABLE.values().first { it.value == value }
    }
}
enum class TYPE(val value: Int){
    BREAKFAST(0),
    LUNCH(1),
    DINNER(2);
    companion object {
        fun fromInt(value: Int) = TYPE.values().first { it.value == value }
    }
}
enum class FOODTYPE(val value: Int){
    COMMON(0),
    USERDEFINED(1);
    companion object {
        fun fromInt(value: Int) = FOODTYPE.values().first { it.value == value }
    }
}


data class Weight(val date: Int, val weight:Int)

data class Sport(val name:String, val calorie:Int)

data class FatRate(val date: Int, val fatRate: Double)

data class DrinkRecord(val date: Int, val volume: Int)

data class SportRecord(val date: Int, val sport: Sport, val time: Int){
    //返回格式化运动记录日期
    fun getDateFormat(): String{
        return SimpleDateFormat("YYYY年MM月DD日").format((date*1000) as Long)
    }

    //返回格式化运动时长
    fun getTimeFormat(): String{
        if(time >= 3600)
            return String.format("%s小时 %s分 %s秒", time/3600, (time%3600)/60, time%60)
        else
            return String.format("%s分 %s秒", (time%3600)/60, time%60)
    }
}

data class Food(val name: String, val unit:String, val calorie: Int, val carbohydrate: Int, val protein: Int, val fat: Int, var type: FOODTYPE)

data class DietRecord(val date: Int, val type: TYPE, val food: Food, val quantity: Double){
    //返回格式化运动记录日期
    fun getDateFormat(): String{
        return SimpleDateFormat("YYYY年MM月DD日").format((date*1000) as Long)
    }
}
