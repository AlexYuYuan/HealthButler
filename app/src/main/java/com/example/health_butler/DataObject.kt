package com.example.health_butler

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


data class Weight(val date: Int, val weight:Double, val fatRate: Double){
    //获取格式化体脂率，保留两位小数
    fun getPercentage(): String{
        val double = (fatRate * 100).toString()
        return double.substring(0, 5) + "%"
    }
}

data class Sport(val name:String, val time:Int)

data class DrinkRecord(val date: Int, val volume: Int)

data class SportRecord(val date: Int, val sportName: String, val time: Int){
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

data class DietRecord(val date: Int, val type: TYPE, val foodName: String, val quantity: Double){
    //返回格式化饮食记录日期
    fun getDateFormat(): String{
        return SimpleDateFormat("YYYY年MM月DD日").format((date*1000) as Long)
    }
}

data class AlarmClock(val time: String, val state: Boolean)

data class BodySize(val date: Int, val chest: Int, val waist: Int, val hip: Int)

data class SportShow(val name:String, val time:Int, val state:Boolean)