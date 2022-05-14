package com.example.health_butler.data

import java.text.SimpleDateFormat

class SportRecord(date: Int, sport: Sport,time: Int) {
    private val date: Int
    private val sport: Sport
    private var time: Int

    init {
        this.date =date
        this.sport = sport
        this.time = time
    }

    //返回格式化运动记录日期
    fun getDateFormat(): String{
        return SimpleDateFormat("YYYY年MM月DD日").format((date*1000) as Long)
    }

    //返回运动记录日期（unix时间秒）
    fun getDate(): Int{
        return date
    }

    //返回运动类型对象
    fun getSport(): Sport{
        return sport
    }

    //返回运动时长（s）
    fun getTime(): Int{
        return time
    }

    //返回格式化运动时长
    fun getTimeFormat(): String{
        if(time >= 3600)
            return String.format("%s小时 %s分 %s秒", time/3600, (time%3600)/60, time%60)
        else
            return String.format("%s分 %s秒", (time%3600)/60, time%60)
    }
}