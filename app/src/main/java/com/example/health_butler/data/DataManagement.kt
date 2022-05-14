package com.example.health_butler.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.util.*

class DataManagement (context: Context){

    private val context = context
    enum class PERIOD{YEAR, MONTH, THREEMONTH}
    enum class TYPE{DIET, SPORT, DRINK}

    private class DataBaseHelper(
        context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?,
        version: Int, errorHandler: DatabaseErrorHandler?
    ) : SQLiteOpenHelper(context, name, factory, version, errorHandler) {

        override fun onCreate(db: SQLiteDatabase?) {

            db?.execSQL("create table foods(food_id int not null primary key," +
                            "food_name varchar(100) not null unique default''," +
                            "type varchar(10) not null unique default''," +
                            "unit varchar(10) not null default'g', " +
                            "calorie int not null default 0, " +
                            "carbohydrate int not null default 0," +
                            "protein int not null default 0," +
                            "fat int not null default 0)")

            db?.execSQL("create table diet_records(recods_id int not null primary key," +
                            "date int not null default 0," +
                            "type varchar(10) not null unique default''," +
                            "food_name INT CONSTRAINT f_fr_name REFERENCES foods(food_name), " +
                            "weight double not null default 0)")

            db?.execSQL("create table sports(sport_name varchar(40) primary key," +
                            "calorie int not null default 0)")

            db?.execSQL("create table sport_records(date int primary key," +
                            "sport_name varchar(40) CONSTRAINT s_sr_name REFERENCES sports(sport_name), " +
                            "time int not null default 0)")

            db?.execSQL("create table weight(date int primary key," +
                            "weight double not null default 0)")

            db?.execSQL("create table fat_rate(date int primary key," +
                            "rate double not null default 0)")

            db?.execSQL("create table body_size(date int primary key," +
                            "chest double not null default 0, " +
                            "waist double not null default 0, " +
                            "hip double not null default 0)")

            db?.execSQL("create table drink_records(date int primary key," +
                    "volume int not null default 0)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    }

    //查找所有食品
    fun queryAllFoods(): LinkedList<Food> {
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val foodsList: LinkedList<Food> = LinkedList<Food>()
        val result = dataBase.query("foods", null, null, null, null, null, "date")
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7)))
            result.moveToNext()
        }
        result.close()
        return foodsList
    }

    //查找用户自定义食品
    fun queryUserDefinedFoods():LinkedList<Food>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val foodsList: LinkedList<Food> = LinkedList<Food>()
        val result = dataBase.query("foods", null, "type = ?", arrayOf("userDefined "), null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7)))
            result.moveToNext()
        }
        result.close()
        return foodsList
    }

    //添加食品，成功返回1，若食品名已存在返回0
    fun insertFoods(food: Food): Int{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val result = dataBase.query("foods", null, "food_name = ?", arrayOf(food.getName()), null, null, null)
        if (result.getCount() != 0){
            val contentValues = ContentValues()
            contentValues.put("name", food.getName())
            contentValues.put("type", food.getType())
            contentValues.put("unit", food.getUnit())
            contentValues.put("calorie", food.getCalorie())
            contentValues.put("carbohydrate", food.getCarbohydrate())
            contentValues.put("protein", food.getProtein())
            contentValues.put("fat", food.getFat())
            dataBase.insert("foods", null, contentValues)
            result.close()
            dataBase.close()
            return 1
        }
        else {
            result.close()
            dataBase.close()
            return 0
        }
    }

    //删除指定用户自定义食品
    fun delUserDefinedFood(food: Food){
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        dataBase.delete("foods","food_name = ?", arrayOf(food.getName()))
        dataBase.close()
    }

    //查询所有运动记录
    fun queryAllSportRecords(): LinkedList<SportRecord>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val sportRecords = LinkedList<SportRecord>()
        val result = dataBase.query("sport_records as sr inner join sport as s", arrayOf("date", "sport_name", "calorie", "time"), "sr.sport_name = s.sport_name", null, null, null, "date")
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            sportRecords.add(SportRecord(result.getInt(0), Sport(result.getString(1), result.getInt(2)), result.getInt(3)))
            result.moveToNext()
        }
        result.close()
        return sportRecords
    }

    //新增运动记录
    fun insertSportRecord(sportRecord: SportRecord){
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val contentValues = ContentValues()

        contentValues.put("date", sportRecord.getDate())
        contentValues.put("sport_name", sportRecord.getSport().name)
        contentValues.put("time", sportRecord.getTime())
        dataBase.insert("sport_records", null, contentValues)

        dataBase.close()
    }

    //新增运动项目,成功返回1，运动名已存在返回2
    fun insertSport(sport: Sport):Int{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val result = dataBase.query("sport", null, "name = ?", arrayOf(sport.name), null, null, null)
        if(result.getCount() != 0){
            val contentValues = ContentValues()
            contentValues.put("sport_name", sport.name)
            contentValues.put("calorie", sport.calorie)
            dataBase.insert("sport", null, contentValues)
            result.close()
            dataBase.close()
            return 1
        }
        else {
            result.close()
            dataBase.close()
            return 0
        }
    }

    //查询体重记录
    fun queryWeights():LinkedList<Weight>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val weights = LinkedList<Weight>()
        val result = dataBase.query("weight", null, null, null, null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            weights.add(Weight(result.getInt(0),result.getInt(1)))
            result.moveToNext()
        }
        result.close()
        return weights
    }

    //新增体重记录
    fun insertWeight(weight: Weight){
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val contentValues = ContentValues()
        contentValues.put("date", weight.date)
        contentValues.put("weight", weight.weight)
        dataBase.insert("weight", null, contentValues)
        dataBase.close()
    }

    //查询所有体脂记录
    fun queryFatRate(): LinkedList<FatRate>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val fatRates = LinkedList<FatRate>()
        val result = dataBase.query("fat_rate", null, null, null, null, null, null)
        dataBase.close()
        while (!result.isAfterLast) {
            fatRates.add(FatRate(result.getInt(0), result.getDouble(1)))
        }
        return fatRates
    }

    //按时间段查询体脂记录
    fun queryFatRate(model:Int): LinkedList<FatRate>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val fatRates = LinkedList<FatRate>()
        val now = LocalDate.now()
        val calendar = Calendar.getInstance()
        calendar.set(now.year, now.monthValue-3, now.dayOfMonth)
        var nowUNIX: Int = (calendar.timeInMillis/1000) as Int
        var begin = 0
        when(model){
            0 -> begin = nowUNIX-31536000
            1 -> begin = nowUNIX-2592000
            2 -> begin = nowUNIX-7776000
        }
        val result = dataBase.query("fat_rate", null, "date >= ? and date <= ?", arrayOf(begin.toString(), nowUNIX.toString()), null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast) {
            fatRates.add(FatRate(result.getInt(0), result.getDouble(1)))
            result.moveToNext()
        }
        result.close()
        return fatRates
    }

    //查询已记录日期
    fun recordDates(type: Int):LinkedList<Int>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val dates = LinkedList<Int>()
        var table: String = ""
        when(type){
            0 -> table = "diet_records"
            1 -> table = "sport_records"
            2 -> table = "drink_records"
        }
        val result = dataBase.query(table, arrayOf("date"), null, null, null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            dates.add(result.getInt(0))
            result.moveToNext()
        }
        result.close()
        return dates
    }


}